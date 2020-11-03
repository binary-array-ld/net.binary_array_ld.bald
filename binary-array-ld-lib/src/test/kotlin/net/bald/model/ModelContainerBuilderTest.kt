package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.*
import net.bald.Container
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.RDF
import org.junit.jupiter.api.Test

class ModelContainerBuilderTest {
    private val model = ModelFactory.createDefaultModel()
    private val parent = model.createResource("http://test.binary-array-ld.net/example")
    private val varBuilder = mock<ModelVarBuilder>()
    private val varFct = mock<ModelVarBuilder.Factory> {
        on { forContainer(any()) } doReturn varBuilder
    }
    private val builderFct = ModelContainerBuilder.Factory(varFct)

    private val vars = listOf<Var>(mock(), mock(), mock())
    private val container = mock<Container> {
        on { vars() } doReturn vars.asSequence()
        on { subContainers() } doReturn emptySequence()
    }

    @Test
    fun addContainer_addsContainerToModel() {
        builderFct.forParent(parent).addContainer(container)
        ResourceVerifier(parent).statements {
            statement(BALD.contains, model.createResource("${parent.uri}/")) {
                statement(RDF.type, BALD.Container)
            }
        }
    }

    @Test
    fun addContainer_addsVars() {
        builderFct.forParent(parent).addContainer(container)
        verify(varFct).forContainer(model.createResource("${parent.uri}/"))
        verify(varBuilder).addVar(vars[0])
        verify(varBuilder).addVar(vars[1])
        verify(varBuilder).addVar(vars[2])
    }

    @Test
    fun addContainer_parentWithTrailingSlash_addsContainerToModel() {
        val parent = model.createResource("http://test.binary-array-ld.net/example/")
        container.stub {
            on { name } doReturn "foo"
        }
        builderFct.forParent(parent).addContainer(container)
        ResourceVerifier(parent).statements {
            statement(BALD.contains, model.createResource("${parent.uri}foo")) {
                statement(RDF.type, BALD.Container)
            }
        }
    }
}