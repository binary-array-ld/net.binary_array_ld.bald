package net.bald.netcdf

import bald.netcdf.CdlConverter
import org.apache.jena.rdf.model.RDFList
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReferenceValueParserTest {
    private val uri = "http://test.binary-array-ld.net/example"
    private val ba: NetCdfBinaryArray = fromCdl()
    private val parser = ReferenceValueParser(ba.root)

    private fun fromCdl(): NetCdfBinaryArray {
        val file = CdlConverter.writeToNetCdf("/netcdf/paths.cdl")
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    @Test
    fun singleValue_returnsResource() {
        val results = parser.parse("var0")
        assertNotNull(results) {
            assertEquals(1, it.size)
            assertEquals("$uri/var0", it.single().uri)
        }
    }

    @Test
    fun singleValue_varNotLocated_returnsNull() {
        val results = parser.parse("varx")
        assertNull(results)
    }

    @Test
    fun unorderedSet_returnsResources() {
        val results = parser.parse("var0 var1 foo/var2")
        assertNotNull(results) {
            assertEquals(3, it.size)
            assertEquals("$uri/var0", it[0].uri)
            assertEquals("$uri/var1", it[1].uri)
            assertEquals("$uri/foo/var2", it[2].uri)
        }
    }

    @Test
    fun unorderedSet_withExtraWhitespace_returnsResources() {
        val results = parser.parse(" var0  var1   foo/var2  ")
        assertNotNull(results) {
            assertEquals(3, it.size)
            assertEquals("$uri/var0", it[0].uri)
            assertEquals("$uri/var1", it[1].uri)
            assertEquals("$uri/foo/var2", it[2].uri)
        }
    }

    @Test
    fun unorderedSet_varNotLocated_returnsNull() {
        val results = parser.parse("var0 var1 varx")
        assertNull(results)
    }

    @Test
    fun orderedList_returnsRdfList() {
        val results = parser.parse("(var0 var1 foo/var2)")
        assertNotNull(results) {
            assertEquals(1, it.size)
            val list = it.single() as RDFList
            val nodes = list.asJavaList()
            assertEquals("$uri/var0", nodes[0].asResource().uri)
            assertEquals("$uri/var1", nodes[1].asResource().uri)
            assertEquals("$uri/foo/var2", nodes[2].asResource().uri)
        }
    }

    @Test
    fun orderedList_withExtraWhitespace_returnsRdfList() {
        val results = parser.parse(" ( var0 var1  foo/var2 ) ")
        assertNotNull(results) {
            assertEquals(1, it.size)
            val list = it.single() as RDFList
            val nodes = list.asJavaList()
            assertEquals("$uri/var0", nodes[0].asResource().uri)
            assertEquals("$uri/var1", nodes[1].asResource().uri)
            assertEquals("$uri/foo/var2", nodes[2].asResource().uri)
        }
    }

    @Test
    fun orderedList_varNotLocated_returnsNull() {
        val results = parser.parse("(var0 var1 varx)")
        assertNull(results)
    }
}