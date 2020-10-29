package net.bald.netcdf

import net.bald.vocab.BALD
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UriParserTest {
    private val prefix = PrefixMapping.Factory.create()
        .setNsPrefix("bald", BALD.prefix)
        .setNsPrefix("skos", SKOS.uri)
        .setNsPrefix("dct", DCTerms.NS)
    private val parser = UriParser(prefix)

    @Test
    fun parse_withSingleUnderscore_returnsNull() {
        val result = parser.parse("skos_prefLabel")
        assertNull(result)
    }

    @Test
    fun parse_withDoubleUnderscore_returnsExpandedUri() {
        val result = parser.parse("bald__contains")
        assertEquals(BALD.contains.uri, result)
    }

    @Test
    fun parse_withLeadingDoubleUnderscore_returnsExpandedUri() {
        prefix.setNsPrefix("", "http://example.org/vocab/")
        val result = parser.parse("__foo")
        assertEquals("http://example.org/vocab/foo", result)
    }

    @Test
    fun parse_withTrailingDoubleUnderscore_returnsExpandedUri() {
        val result = parser.parse("skos__")
        assertEquals(SKOS.uri, result)
    }

    @Test
    fun parse_prefixNotFound_returnsNull() {
        val result = parser.parse("foo__bar")
        assertNull(result)
    }
}