package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.Attribute
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
    private val attrBuilder = mock<ModelAttributeBuilder>()
    private val attrFct = mock<ModelAttributeBuilder.Factory> {
        on { forResource(any()) } doReturn attrBuilder
    }
    private val builder = ModelContainerBuilder.Factory(varFct, attrFct).forBinaryArray(ba)

    private val vars = listOf<Var>(mock(), mock(), mock())
    private val attrs = listOf<Attribute>(mock(), mock(), mock())
    private val container = mock<Container> {
        on { vars() } doReturn vars.asSequence()
        on { attributes(any()) } doReturn attrs
    }

    @Test
    fun addContainer_addsContainerToModel() {
        builder.addContainer(container)
        ResourceVerifier(ba).statements {
            statement(BALD.contains, model.createResource("${ba.uri}/")) {
                statement(RDF.type, BALD.Container)
            }
        }
    }

    @Test
    fun addContainer_addsVars() {
        builder.addContainer(container)
        verify(varFct).forContainer(model.createResource("${ba.uri}/"))
        verify(varBuilder).addVar(vars[0])
        verify(varBuilder).addVar(vars[1])
        verify(varBuilder).addVar(vars[2])
    }

    @Test
    fun addContainer_addsAttributes() {
        builder.addContainer(container)
        verify(container).attributes(model)
        verify(attrFct).forResource(model.createResource("${ba.uri}/"))
        verify(attrBuilder).addAttribute(attrs[0])
        verify(attrBuilder).addAttribute(attrs[1])
        verify(attrBuilder).addAttribute(attrs[2])
    }
}