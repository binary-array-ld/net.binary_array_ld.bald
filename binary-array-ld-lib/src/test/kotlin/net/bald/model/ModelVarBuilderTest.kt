package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.vocabulary.RDF
import org.junit.jupiter.api.*

class ModelVarBuilderTest {
    private val model = ModelFactory.createDefaultModel()
    private val container = model.createResource("http://test.binary-array-ld.net/example/")
    private val builder = ModelVarBuilder.Factory().forContainer(container)

    @Test
    fun addVar_addsResourceToContainer() {
        val v = mock<Var> { on { name } doReturn "foo" }
        builder.addVar(v)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
            }
        }
    }

    @Test
    fun addVar_multiple_addsResourcesToContainer() {
        val v1 = mock<Var> { on { name } doReturn "foo" }
        val v2 = mock<Var> { on { name } doReturn "bar" }
        val v3 = mock<Var> { on { name } doReturn "baz" }

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

        val v = mock<Var> { on { name } doReturn "foo" }
        ModelVarBuilder.Factory().forContainer(container).addVar(v)

        ResourceVerifier(container).statements {
            statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                statement(RDF.type, BALD.Resource)
            }
        }
    }
}