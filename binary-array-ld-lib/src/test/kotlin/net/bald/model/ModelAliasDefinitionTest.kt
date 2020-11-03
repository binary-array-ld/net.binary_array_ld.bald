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
        val result = alias.propertyUri("undefined")
        assertNull(result)
    }

    @Test
    fun propertyUri_resourceAliasDefined_returnsNull() {
        val result = alias.propertyUri("binary-array-ld-org")
        assertNull(result)
    }

    @Test
    fun propertyUri_propertyAliasDefined_returnsUri() {
        val result = alias.propertyUri("name")
        assertEquals(RDFS.label.uri, result)
    }

    @Test
    fun propertyUri_objectPropertyAliasDefined_returnsUri() {
        val result = alias.propertyUri("dct_publisher")
        assertEquals(DCTerms.publisher.uri, result)
    }

    @Test
    fun resourceUri_aliasNotDefined_returnsNull() {
        val result = alias.resourceUri("undefined")
        assertNull(result)
    }

    @Test
    fun resourceUri_resourceAliasDefined_returnsUri() {
        val result = alias.resourceUri("binary-array-ld-org")
        assertEquals(BALD.prefix + "Organisation", result)
    }

    @Test
    fun resourceUri_propertyAliasDefined_returnsUri() {
        val result = alias.resourceUri("name")
        assertEquals(RDFS.label.uri, result)
    }

    @Test
    fun resourceUri_objectPropertyAliasDefined_returnsUri() {
        val result = alias.resourceUri("dct_publisher")
        assertEquals(DCTerms.publisher.uri, result)
    }
}