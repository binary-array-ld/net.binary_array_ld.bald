package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.Attribute
import net.bald.CoordinateRange
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import org.junit.jupiter.api.*

class ModelVarBuilderTest {
    private val model = ModelFactory.createDefaultModel()
    private val container = model.createResource("http://test.binary-array-ld.net/example/")
    private val attrBuilder = mock<ModelAttributeBuilder>()
    private val attrFct = mock<ModelAttributeBuilder.Factory> {
        on { forResource(any()) } doReturn attrBuilder
    }
    private val builder = ModelVarBuilder.Factory(attrFct).forContainer(container)

    private fun newVar(
        uri: String,
        attrs: List<Attribute> = emptyList(),
        dims: List<Dimension> = emptyList()
    ): Var {
        return mock {
            on { this.uri } doReturn uri
            on { attributes() } doReturn attrs
            on { dimensions() } doReturn dims.asSequence()
        }
    }

    @Test
    fun addVar_addsResourceToContainer() {
        val v = newVar("http://test.binary-array-ld.net/example/foo")
        builder.addVar(v)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
            }
        }
    }

    @Test
    fun addVar_multiple_addsResourcesToContainer() {
        val v1 = newVar("http://test.binary-array-ld.net/example/foo")
        val v2 = newVar("http://test.binary-array-ld.net/example/bar")
        val v3 = newVar("http://test.binary-array-ld.net/example/baz")

        builder.addVar(v1)
        builder.addVar(v2)
        builder.addVar(v3)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/bar")) {
                statement(RDF.type, BALD.Resource)
            }
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/baz")) {
                statement(RDF.type, BALD.Resource)
            }
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
            }
        }
    }

    @Test
    fun addVar_addsAttributes() {
        val attrs = listOf<Attribute>(
            mock { on { uri } doReturn RDF.type.uri },
            mock { on { uri } doReturn RDFS.label.uri }
        )
        val v = newVar("http://test.binary-array-ld.net/example/foo", attrs)
        builder.addVar(v)

        verify(v).attributes()
        verify(attrFct).forResource(model.createResource("http://test.binary-array-ld.net/example/foo"))
        verify(attrBuilder).addAttribute(attrs[0])
        verify(attrBuilder).addAttribute(attrs[1])
    }

    @Test
    fun addVar_addsDimensions() {
        val dims = listOf<Dimension>(
            mock { on { size } doReturn 10 },
            mock { on { size } doReturn 30 },
            mock { on { size } doReturn 1000 }
        )
        val v = newVar("http://test.binary-array-ld.net/example/foo", dims = dims)
        builder.addVar(v)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Array)
                statement(BALD.shape) {
                    list(
                        createTypedLiteral(10),
                        createTypedLiteral(30),
                        createTypedLiteral(1000)
                    )
                }
            }
        }
    }

    @Test
    fun addVar_withCoordinateRange_addsCoordinateRange() {
        val range = mock<CoordinateRange> {
            on { first } doReturn 0.5
            on { last } doReturn 100.5
        }
        val v = mock<Var> {
            on { uri } doReturn "http://test.binary-array-ld.net/example/foo"
            on { dimensions() } doReturn emptySequence()
            on { this.range } doReturn range
        }
        builder.addVar(v)
        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
                statement(BALD.arrayFirstValue, createTypedLiteral("0.5", XSDDatatype.XSDdouble))
                statement(BALD.arrayLastValue, createTypedLiteral("100.5", XSDDatatype.XSDdouble))
            }
        }
    }

    @Test
    fun addVar_withCoordinateRange_withoutLastValue_addsCoordinateRange() {
        val range = mock<CoordinateRange> {
            on { first } doReturn 0.5
        }
        val v = mock<Var> {
            on { uri } doReturn "http://test.binary-array-ld.net/example/foo"
            on { dimensions() } doReturn emptySequence()
            on { this.range } doReturn range
        }
        builder.addVar(v)
        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
                statement(BALD.arrayFirstValue, createTypedLiteral("0.5", XSDDatatype.XSDdouble))
            }
        }
    }

    @Test
    fun addVar_withDimensions_withCoordinates_addsReference() {
        val coord1 = newVar("http://test.binary-array-ld.net/example/bar")
        val dim1 = mock<Dimension> {
            on { size } doReturn 10
            on { coordinate } doReturn coord1
        }
        val coord2 = newVar("http://test.binary-array-ld.net/example/baz")
        val dim2 = mock<Dimension> {
            on { size } doReturn 90
            on { coordinate } doReturn coord2
        }
        val v = newVar("http://test.binary-array-ld.net/example/foo", dims = listOf(dim1, dim2))
        builder.addVar(v)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Array)
                statement(BALD.references) {
                    statement(RDF.type, BALD.Reference)
                    statement(BALD.reshape) {
                        list(createTypedLiteral(10), createTypedLiteral(1))
                    }
                    statement(BALD.target, createResource("http://test.binary-array-ld.net/example/bar"))
                    statement(BALD.targetShape) {
                        list(createTypedLiteral(10))
                    }
                }
                statement(BALD.references) {
                    statement(RDF.type, BALD.Reference)
                    statement(BALD.reshape) {
                        list(createTypedLiteral(1), createTypedLiteral(90))
                    }
                    statement(BALD.target, createResource("http://test.binary-array-ld.net/example/baz"))
                    statement(BALD.targetShape) {
                        list(createTypedLiteral(90))
                    }
                }
                statement(BALD.shape) {
                    list(createTypedLiteral(10), createTypedLiteral(90))
                }
            }
        }
    }
}