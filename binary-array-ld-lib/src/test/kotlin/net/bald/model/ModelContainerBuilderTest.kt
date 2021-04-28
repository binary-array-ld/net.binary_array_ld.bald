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
    private val parent = model.createResource("http://test.binary-array-ld.net/example")
    private val varBuilder = mock<ModelVarBuilder>()
    private val varFct = mock<ModelVarBuilder.Factory> {
        on { forContainer(any()) } doReturn varBuilder
    }
    private val attrBuilder = mock<ModelAttributeBuilder>()
    private val attrFct = mock<ModelAttributeBuilder.Factory> {
        on { forResource(any()) } doReturn attrBuilder
    }
    private val builder = ModelContainerBuilder.Factory(varFct, attrFct).forParent(parent)

    private val vars = listOf<Var>(mock(), mock(), mock())
    private val attrs = listOf<Attribute>(mock(), mock(), mock())
    private val container = mock<Container> {
        on { uri } doReturn "http://test.binary-array-ld.net/example/foo"
        on { vars() } doReturn vars.asSequence()
        on { attributes() } doReturn attrs.asSequence()
        on { subContainers() } doReturn emptySequence()
    }

    @Test
    fun addContainer_addsContainerToModel() {
        builder.addContainer(container)
        ResourceVerifier(parent).statements {
            statement(BALD.contains, model.createResource("${parent.uri}/foo")) {
                statement(RDF.type, BALD.Container)
            }
        }
    }

    @Test
    fun addContainer_addsVars() {
        builder.addContainer(container)
        verify(varFct).forContainer(model.createResource("${parent.uri}/foo"))
        verify(varBuilder).addVar(vars[0])
        verify(varBuilder).addVar(vars[1])
        verify(varBuilder).addVar(vars[2])
    }

    @Test
    fun addContainer_addsAttributes() {
        builder.addContainer(container)
        verify(container).attributes()
        verify(attrFct).forResource(model.createResource("${parent.uri}/foo"))
        verify(attrBuilder).addAttribute(attrs[0])
        verify(attrBuilder).addAttribute(attrs[1])
        verify(attrBuilder).addAttribute(attrs[2])
    }
}