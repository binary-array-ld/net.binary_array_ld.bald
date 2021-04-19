package net.bald.model

import net.bald.alias.ModelAliasDefinition
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createProperty
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDFS
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    /**
     * Requirements class C-3
     */
    @Test
    fun propertyUri_propertyAliasDefined_returnsProperty() {
        val result = alias.property("name")
        assertEquals(RDFS.label, result)
    }

    /**
     * Requirements class C-3
     */
    @Test
    fun propertyUri_objectPropertyAliasDefined_returnsProperty() {
        val result = alias.property("dct_publisher")
        assertEquals(DCTerms.publisher, result)
    }

    @Test
    fun isReferenceProperty_withoutDefinition_returnsFalse() {
        val result = alias.isReferenceProperty(SKOS.broader)
        assertFalse(result)
    }

    @Test
    fun isReferenceProperty_withDefinition_withoutSubjectRange_returnsFalse() {
        val result = alias.isReferenceProperty(DCTerms.publisher)
        assertFalse(result)
    }

    @Test
    fun isReferenceProperty_withSubjectRange_returnsTrue() {
        val result = alias.isReferenceProperty(BALD.references)
        assertTrue(result)
    }

    @Test
    fun isReferenceProperty_withSubjectDirectSubClassRange_returnsTrue() {
        val prop = createProperty("http://test.binary-array-ld.net/vocab/direct")
        val result = alias.isReferenceProperty(prop)
        assertTrue(result)
    }

    @Test
    fun isReferenceProperty_withSubjectIndirectSubClassRange_returnsTrue() {
        val prop = createProperty("http://test.binary-array-ld.net/vocab/indirect")
        val result = alias.isReferenceProperty(prop)
        assertTrue(result)
    }

    @Test
    fun isReferenceProperty_circularClassHierarchy_returnsFalse() {
        val model = ModelFactory.createDefaultModel().apply {
            javaClass.getResourceAsStream("/alias/circular.ttl").use { ttl ->
                read(ttl, null, "ttl")
            }
        }
        val alias = ModelAliasDefinition.create(model)
        assertEquals(false, alias.isReferenceProperty(createProperty("http://test.binary-array-ld.net/vocab/direct")))
    }

    /**
     * Requirements class C-2
     */
    @Test
    fun propertyUri_withConflict_throwsException() {
        val model = ModelFactory.createDefaultModel().apply {
            javaClass.getResourceAsStream("/alias/conflict.ttl").use { ttl ->
                read(ttl, null, "ttl")
            }
        }
        val alias = ModelAliasDefinition.create(model)
        val ise = assertThrows<IllegalStateException> {
            alias.property("name")
        }
        assertEquals("Property alias name is ambiguous: [${SKOS.prefLabel}, ${RDFS.label}]", ise.message)
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

    /**
     * Requirements class C-2
     */
    @Test
    fun resourceUri_propertyAliasDefined_returnsUri() {
        val result = alias.resource("name")
        assertEquals(RDFS.label, result)
    }

    /**
     * Requirements class C-2
     */
    @Test
    fun resourceUri_objectPropertyAliasDefined_returnsUri() {
        val result = alias.resource("dct_publisher")
        assertEquals(DCTerms.publisher, result)
    }

    /**
     * Requirements class C-2
     */
    @Test
    fun resourceUri_withConflict_throwsException() {
        val model = ModelFactory.createDefaultModel().apply {
            javaClass.getResourceAsStream("/alias/conflict.ttl").use { ttl ->
                read(ttl, null, "ttl")
            }
        }
        val alias = ModelAliasDefinition.create(model)
        val ise = assertThrows<IllegalStateException> {
            alias.resource("binary-array-ld-org")
        }
        assertEquals("Resource alias binary-array-ld-org is ambiguous: [${BALD.prefix}Org, ${BALD.prefix}Organisation]", ise.message)
    }
}