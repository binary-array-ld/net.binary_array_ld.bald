package net.bald.model

import bald.model.ModelVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.BinaryArray
import net.bald.Container
import net.bald.PrefixMapping
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test

class ModelBinaryArrayBuilderTest {
    private val containerBuilder = mock<ModelContainerBuilder>()
    private val containerFct = mock<ModelContainerBuilder.Factory> {
        on { forBinaryArray(any()) } doReturn containerBuilder
    }
    private val model = ModelFactory.createDefaultModel()
    private val builder = ModelBinaryArrayBuilder.Factory(containerFct).forModel(model)
    private val root = mock<Container>()
    private val prefix = mock<PrefixMapping> {
        on { toMap() } doReturn mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri
        )
    }
    private val ba = mock<BinaryArray> {
        on { uri } doReturn "http://test.binary-array-ld.net/example"
        on { this.root } doReturn root
        on { prefixMapping } doReturn prefix
    }

    @Test
    fun addBinaryArray_addsFileContainer() {
        builder.addBinaryArray(ba)
        ModelVerifier(model).resource("http://test.binary-array-ld.net/example") {
            statement(RDF.type, BALD.Container)
        }
    }

    @Test
    fun addBinaryArray_addsRootContainer() {
        builder.addBinaryArray(ba)
        verify(containerFct).forBinaryArray(model.getResource("http://test.binary-array-ld.net/example"))
        verify(containerBuilder).addContainer(root)
    }

    @Test
    fun addBinaryArray_addsPrefixMapping() {
        builder.addBinaryArray(ba)
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
        }
    }
}