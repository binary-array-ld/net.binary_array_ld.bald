package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.Container
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.RDF
import org.junit.jupiter.api.Test

class ModelContainerBuilderTest {
    private val model = ModelFactory.createDefaultModel()
    private val ba = model.createResource("http://test.binary-array-ld.net/example")
    private val varBuilder = mock<ModelVarBuilder>()
    private val varFct = mock<ModelVarBuilder.Factory> {
        on { forContainer(any()) } doReturn varBuilder
    }
    private val uri = "http://test.binary-array-ld.net/example"
    private val builderFct = ModelContainerBuilder.Factory(varFct)

    private val vars = listOf<Var>(mock(), mock(), mock())
    private val container = mock<Container> {
        on { vars() } doReturn vars.asSequence()
    }

    @Test
    fun addContainer_addsContainerToModel() {
        builderFct.forBinaryArray(ba).addContainer(container)
        ResourceVerifier(ba).statements {
            statement(RDF.type, BALD.Container)
        }
    }

    @Test
    fun addContainer_uriWithTrailingSlash_addsContainerToModel() {
        val ba = model.createResource("${ba.uri}/")
        builderFct.forBinaryArray(ba).addContainer(container)
        ResourceVerifier(ba).statements {
            statement(RDF.type, BALD.Container)
        }
    }

    @Test
    fun addContainer_addsVars() {
        builderFct.forBinaryArray(ba).addContainer(container)
        verify(varFct).forContainer(model.createResource("$uri/"))
        verify(varBuilder).addVar(vars[0])
        verify(varBuilder).addVar(vars[1])
        verify(varBuilder).addVar(vars[2])
    }
}