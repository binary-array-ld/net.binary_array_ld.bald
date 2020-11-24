package net.bald.model

import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDFS
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelAliasDefinitionTest {
    private val model = ModelFactory.createDefaultModel().apply {
        javaClass.getResourceAsStream("/alias/alias.ttl").use { ttl ->
            read(ttl, null, "ttl")
        }
    }
    private val alias = ModelAliasDefinition.create(model)

    @Test
    fun propertyUri_aliasNotDefined_returnsNull() {
        val result = alias.property("undefined")
        assertNull(result)
    }

    @Test
    fun propertyUri_resourceAliasDefined_returnsNull() {
        val result = alias.property("binary-array-ld-org")
        assertNull(result)
    }

    @Test
    fun propertyUri_propertyAliasDefined_returnsUri() {
        val result = alias.property("name")
        assertEquals(RDFS.label, result)
    }

    @Test
    fun propertyUri_objectPropertyAliasDefined_returnsUri() {
        val result = alias.property("dct_publisher")
        assertEquals(DCTerms.publisher, result)
    }

    @Test
    fun resourceUri_aliasNotDefined_returnsNull() {
        val result = alias.resource("undefined")
        assertNull(result)
    }

    @Test
    fun resourceUri_resourceAliasDefined_returnsUri() {
        val result = alias.resource("binary-array-ld-org")
        assertEquals(BALD.prefix + "Organisation", result?.uri)
    }

    @Test
    fun resourceUri_propertyAliasDefined_returnsUri() {
        val result = alias.resource("name")
        assertEquals(RDFS.label, result)
    }

    @Test
    fun resourceUri_objectPropertyAliasDefined_returnsUri() {
        val result = alias.resource("dct_publisher")
        assertEquals(DCTerms.publisher, result)
    }
}