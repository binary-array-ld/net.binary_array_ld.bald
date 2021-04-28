package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.vocabulary.RDF
import org.junit.jupiter.api.Test

class ModelReferenceBuilderTest {
    private val sourceDims = listOf(
        newDimension("dim1", 10),
        newDimension("dim2", 90),
        newDimension("dim3", 15)
    )
    private val v = mock<Var> {
        on { dimensions() } doReturn sourceDims.asSequence()
    }
    private val model = ModelFactory.createDefaultModel()
    private val vRes = model.createResource("http://test.binary-array-ld.net/example")
    private val builder = ModelReferenceBuilder.Factory().forVar(v, vRes)

    private fun newVar(uri: String, dims: List<Dimension>): Var {
        return mock {
            on { this.uri } doReturn uri
            on { this.dimensions() } doReturn dims.asSequence()
        }
    }

    private fun newDimension(name: String, size: Int): Dimension {
        return mock {
            on { this.name } doReturn name
            on { this.size } doReturn size
        }
    }

    @Test
    fun addReference_coordinateVar() {
        val coordinate = newVar("http://test.binary-array-ld.net/example/foo", listOf(sourceDims[1]))
        builder.addReference(coordinate)

        ResourceVerifier(vRes).statements {
            statement(BALD.references) {
                statement(RDF.type, BALD.Reference)
                statement(BALD.sourceRefShape) {
                    list(10, 90, 15)
                }
                statement(BALD.target, createResource("http://test.binary-array-ld.net/example/foo"))
                statement(BALD.targetRefShape) {
                    list(1, 90, 1)
                }
            }
        }
    }

    @Test
    fun addReference_lowerDimensionVar() {
        val coordinate = newVar("http://test.binary-array-ld.net/example/foo", sourceDims.subList(0, 2))
        builder.addReference(coordinate)

        ResourceVerifier(vRes).statements {
            statement(BALD.references) {
                statement(RDF.type, BALD.Reference)
                statement(BALD.sourceRefShape) {
                    list(10, 90, 15)
                }
                statement(BALD.target, createResource("http://test.binary-array-ld.net/example/foo"))
                statement(BALD.targetRefShape) {
                    list(10, 90, 1)
                }
            }
        }
    }

    @Test
    fun addReference_higherDimensionVar() {
        val higherDims = listOf(
            newDimension("dim4", 10),
            newDimension("dim5", 60)
        )
        val coordinate = newVar("http://test.binary-array-ld.net/example/foo", sourceDims + higherDims)
        builder.addReference(coordinate)

        ResourceVerifier(vRes).statements {
            statement(BALD.references) {
                statement(RDF.type, BALD.Reference)
                statement(BALD.sourceRefShape) {
                    list(10, 90, 15, 1, 1)
                }
                statement(BALD.target, createResource("http://test.binary-array-ld.net/example/foo"))
                statement(BALD.targetRefShape) {
                    list(10, 90, 15, 10, 60)
                }
            }
        }
    }

    @Test
    fun addReference_disjointDimensionVar() {
        val targetDims = listOf(
            newDimension("dim4", 10),
            newDimension("dim5", 60)
        )
        val coordinate = newVar("http://test.binary-array-ld.net/example/foo", targetDims)
        builder.addReference(coordinate)

        ResourceVerifier(vRes).statements {
            statement(BALD.references) {
                statement(RDF.type, BALD.Reference)
                statement(BALD.sourceRefShape) {
                    list(10, 90, 15, 1, 1)
                }
                statement(BALD.target, createResource("http://test.binary-array-ld.net/example/foo"))
                statement(BALD.targetRefShape) {
                    list(1, 1, 1, 10, 60)
                }
            }
        }
    }

    @Test
    fun addReference_partiallyDisjointDimensionVar() {
        val targetDims = listOf(
            sourceDims[1],
            sourceDims[2],
            newDimension("dim4", 10),
            newDimension("dim5", 60)
        )
        val coordinate = newVar("http://test.binary-array-ld.net/example/foo", targetDims)
        builder.addReference(coordinate)

        ResourceVerifier(vRes).statements {
            statement(BALD.references) {
                statement(RDF.type, BALD.Reference)
                statement(BALD.sourceRefShape) {
                    list(10, 90, 15, 1, 1)
                }
                statement(BALD.target, createResource("http://test.binary-array-ld.net/example/foo"))
                statement(BALD.targetRefShape) {
                    list(1, 90, 15, 10, 60)
                }
            }
        }
    }
}