package net.bald.context

import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.SKOS
import org.apache.jena.vocabulary.VCARD
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ModelContextTest {
    private val prefix = PrefixMapping.Factory.create()
        .setNsPrefix("skos", SKOS.uri)
        .setNsPrefix("dct", DCTerms.getURI())
    private val context = ModelContext.create(prefix)

    /**
     * Requirements class B-4
     */
    @Test
    fun prefixMapping_returnsPrefixMapping() {
        val result = context.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "skos" to SKOS.uri,
            "dct" to DCTerms.getURI()
        )
        assertEquals(expected, result)
    }

    /**
     * Requirements class B-4
     */
    @Test
    fun prefixMapping_multipleContexts_returnsCombinedPrefixMapping() {
        val prefixes = listOf(
            PrefixMapping.Factory.create()
                .setNsPrefix("skos", SKOS.uri)
                .setNsPrefix("dct", DCTerms.getURI()),
            PrefixMapping.Factory.create()
                .setNsPrefix("vcard", VCARD.uri)
                .setNsPrefix("dct", DCTerms.getURI())
        )
        val context = ModelContext.create(prefixes)
        val result = context.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "skos" to SKOS.uri,
            "dct" to DCTerms.getURI(),
            "vcard" to VCARD.uri
        )
        assertEquals(expected, result)
    }

    /**
     * Requirements class B-7
     */
    @Test
    fun create_multipleContexts_withConflicts_throwsException() {
        val prefixes = listOf(
            PrefixMapping.Factory.create()
                .setNsPrefix("skos", "http://example.org/skos/")
                .setNsPrefix("dct", DCTerms.getURI()),
            PrefixMapping.Factory.create()
                .setNsPrefix("skos", SKOS.uri)
                .setNsPrefix("dct", DCTerms.getURI())
        )
        val iae = assertThrows<IllegalArgumentException> {
            ModelContext.create(prefixes)
        }
        assertEquals("The namespace prefixes [skos] have conflicting definitions in contexts.", iae.message)
    }
}