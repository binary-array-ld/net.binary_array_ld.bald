package net.bald.context

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.bald.BinaryArray
import net.bald.Container
import net.bald.vocab.BALD
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

class ContextBinaryArrayTest {
    private val context = PrefixMapping.Factory.create()
        .setNsPrefix("skos", "http://example.org/skos/")
        .setNsPrefix("dct", DCTerms.getURI())
    private val root = mock<Container>()
    private val ba = mock<BinaryArray> {
        on { uri } doReturn "http://test.binary-array-ld.net/example"
        on { root } doReturn root
        on { prefixMapping } doReturn PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
    }
    private val contextBa = ContextBinaryArray(ba, context)

    @Test
    fun uri_returnsUri() {
        assertEquals("http://test.binary-array-ld.net/example", contextBa.uri)
    }

    @Test
    fun root_returnsRoot() {
        assertEquals(root, contextBa.root)
    }

    @Test
    fun close_closesBinaryArray() {
        contextBa.close()
        verify(ba).close()
    }

    @Test
    fun prefixMapping_returnsCombinedPrefixMapping() {
        val result = contextBa.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri,
            "dct" to DCTerms.getURI()
        )
        assertEquals(expected, result)
    }
}