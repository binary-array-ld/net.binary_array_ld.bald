package net.bald.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.BinaryArray
import net.bald.Container
import org.apache.jena.rdf.model.ModelFactory
import org.junit.jupiter.api.*

class ModelBinaryArrayBuilderTest {
    private val containerBuilder = mock<ModelContainerBuilder>()
    private val containerFct = mock<ModelContainerBuilder.Factory> {
        on { forBinaryArray(any(), any()) } doReturn containerBuilder
    }
    private val model = ModelFactory.createDefaultModel()
    private val builder = ModelBinaryArrayBuilder.Factory(containerFct).forModel(model)

    @Test
    fun addBinaryArray_addsRootContainer() {
        val root = mock<Container>()
        val ba = mock<BinaryArray> {
            on { uri } doReturn "http://test.binary-array-ld.net/example"
            on { this.root } doReturn root
        }
        builder.addBinaryArray(ba)
        verify(containerFct).forBinaryArray(model, "http://test.binary-array-ld.net/example")
        verify(containerBuilder).addContainer(root)
    }
}