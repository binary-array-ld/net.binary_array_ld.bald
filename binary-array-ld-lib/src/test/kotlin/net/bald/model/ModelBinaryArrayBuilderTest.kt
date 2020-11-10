package net.bald.model

import bald.model.ModelVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.BinaryArray
import net.bald.Container
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals

class ModelBinaryArrayBuilderTest {
    private val containerBuilder = mock<ModelContainerBuilder>()
    private val containerFct = mock<ModelContainerBuilder.Factory> {
        on { forParent(any()) } doReturn containerBuilder
    }
    private val model = ModelFactory.createDefaultModel()
    private val builder = ModelBinaryArrayBuilder.Factory(containerFct).forModel(model)
    private val root = mock<Container>()
    private val prefix = PrefixMapping.Factory.create()
        .setNsPrefix("bald", BALD.prefix)
        .setNsPrefix("skos", SKOS.uri)
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
        verify(containerFct).forParent(model.getResource("http://test.binary-array-ld.net/example"))
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

    @Test
    fun addBinaryArray_prefixMappingWithInvalidChar_throwsException() {
        prefix.setNsPrefix("bald-eg", "http://example.org/prefix/")
        val iae = assertThrows<IllegalArgumentException> {
            builder.addBinaryArray(ba)
        }
        assertEquals("Unable to add prefix mapping bald-eg to model: Prefix must match pattern [A-Za-z_]+.", iae.message)
    }

    @Test
    fun addBinaryArray_prefixMappingWithInvalidScheme_throwsException() {
        prefix.setNsPrefix("eg", "file:///example/prefix/")
        val iae = assertThrows<IllegalArgumentException> {
            builder.addBinaryArray(ba)
        }
        assertEquals("Unable to add prefix mapping eg to model: URI must have HTTP or HTTPS scheme.", iae.message)
    }

    @Test
    fun addBinaryArray_prefixMappingWithoutTrailingChar_throwsException() {
        prefix.setNsPrefix("eg", "http://example.org/prefix")
        val iae = assertThrows<IllegalArgumentException> {
            builder.addBinaryArray(ba)
        }
        assertEquals("Unable to add prefix mapping eg to model: URI must end with / or #.", iae.message)
    }
}