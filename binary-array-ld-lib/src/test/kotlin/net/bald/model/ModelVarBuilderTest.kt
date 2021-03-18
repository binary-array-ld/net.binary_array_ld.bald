package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.Attribute
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createResource
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

    private fun newVar(uri: String, attrs: List<Attribute> = emptyList()): Var {
        return mock {
            on { this.uri } doReturn uri
            on { attributes() } doReturn attrs
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
    fun addVar_containerWithoutTrailingSlash_addsResourceToContainer() {
        val container = model.createResource("http://test.binary-array-ld.net/example")

        val v = mock<Var> { on { uri } doReturn "http://test.binary-array-ld.net/example/foo" }
        ModelVarBuilder.Factory(attrFct).forContainer(container).addVar(v)

        ResourceVerifier(container).statements {
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
}