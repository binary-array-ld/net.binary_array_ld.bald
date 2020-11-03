package net.bald.alias

import com.nhaarman.mockitokotlin2.*
import net.bald.Attribute
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AliasAttributeTest {
    private val propertyUri = "http://test.binary-array-ld.net/example/alias/property"
    private val resourceUri = "http://test.binary-array-ld.net/example/alias/resource"
    private val attr = mock<Attribute> {
        on { name } doReturn "foo"
    }
    private val alias = mock<AliasDefinition> {
        on { propertyUri(any()) } doReturn propertyUri
        on { resourceUri(any()) } doReturn resourceUri
    }
    private val aliasAttr = AliasAttribute(attr, alias)

    @Test
    fun uri_attributeWithUri_returnsUri() {
        val uri = "http://test.binary-array-ld.net/example/foo"
        attr.stub {
            on { this.uri } doReturn uri
        }
        assertEquals(uri, aliasAttr.uri)
    }

    @Test
    fun uri_attributeWithoutUri_nameWithAlias_returnsAlias() {
        assertEquals(propertyUri, aliasAttr.uri)
        verify(alias).propertyUri("foo")
    }

    @Test
    fun uri_attributeWithoutUri_nameWithoutAlias_returnsNull() {
        alias.stub {
            on { propertyUri(any()) } doReturn null
        }
        assertNull(aliasAttr.uri)
        verify(alias).propertyUri("foo")
    }

    @Test
    fun name_returnsName() {
        assertEquals("foo", aliasAttr.name)
    }

    @Test
    fun values_resourceValue_returnsValue() {
        val resource = createResource("http://test.binary-array-ld.net/example/foo")
        attr.stub {
            on { values } doReturn listOf(resource)
        }
        val results = aliasAttr.values
        assertEquals(1, results.size)
        assertEquals(resource, results[0])
        verify(alias, never()).resourceUri(any())
    }

    @Test
    fun values_literalValue_withAlias_returnsAlias() {
        val literal = createPlainLiteral("foo")
        attr.stub {
            on { values } doReturn listOf(literal)
        }
        val results = aliasAttr.values
        assertEquals(1, results.size)
        assertEquals(createResource(resourceUri), results[0])
        verify(alias).resourceUri("foo")
    }

    @Test
    fun values_literalValue_withoutAlias_returnsValue() {
        alias.stub {
            on { resourceUri(any()) } doReturn null
        }
        val literal = createPlainLiteral("foo")
        attr.stub {
            on { values } doReturn listOf(literal)
        }
        val results = aliasAttr.values
        assertEquals(1, results.size)
        assertEquals(literal, results[0])
        verify(alias).resourceUri("foo")
    }

    @Test
    fun values_multipleValues_returnsAliases() {
        alias.stub {
            on { resourceUri(any()) } doReturn null
            on { resourceUri("foo") } doReturn "http://test.binary-array-ld.net/example/foo"
        }
        val values = listOf<RDFNode>(
            createPlainLiteral("foo"),
            createResource("http://test.binary-array-ld.net/example/bar"),
            createPlainLiteral("baz")
        )
        attr.stub {
            on { this.values } doReturn values
        }
        val results = aliasAttr.values
        assertEquals(3, results.size)
        assertEquals(createResource("http://test.binary-array-ld.net/example/foo"), results[0])
        assertEquals(values[1], results[1])
        assertEquals(values[2], results[2])
    }
}