package net.bald.netcdf

import bald.netcdf.NcmlConverter.convertToNetCdf
import bald.netcdf.NcmlConverter.writeToNetCdf
import net.bald.BinaryArray
import net.bald.PrefixMapping
import net.bald.vocab.BALD
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NetCdfBinaryArrayTest {
    private fun fromNcml(ncmlLoc: String, uri: String? = null): BinaryArray {
        val file = writeToNetCdf(ncmlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    @Test
    fun uri_withUri_returnsValue() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromNcml("/netcdf/identity.xml", uri)
        assertEquals(uri, ba.uri)
    }

    @Test
    fun uri_withoutUri_returnsFileUri() {
        val netCdfFile = writeToNetCdf("/netcdf/identity.xml")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        val expectedUri = netCdfFile.toPath().toUri().toString()
        assertEquals(expectedUri, ba.uri)
    }

    @Test
    fun root_vars_withVars_returnsVariables() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromNcml("/netcdf/identity.xml", uri)

        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("var0", vars[0].name)
        assertEquals("var1", vars[1].name)
    }

    @Test
    fun prefixMapping_withoutPrefixMapping_returnsEmptyPrefixMapping() {
        val netCdfFile = writeToNetCdf("/netcdf/identity.xml")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        assertEquals(PrefixMapping.Empty, ba.prefixMapping)
    }

    @Test
    fun prefixMapping_withInternalPrefixMapping_returnsPrefixMapping() {
        val netCdf = convertToNetCdf("/netcdf/prefix.xml")
        val ba = NetCdfBinaryArray("http://test.binary-array-ld.net/prefix.nc", netCdf)
        val prefix = ba.prefixMapping.toMap()
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri
        )
        assertEquals(expected, prefix)
    }

    @Test
    fun prefixMapping_prefixGroupDoesNotExist_throwsException() {
        val netCdf = convertToNetCdf("/netcdf/prefix-group-error.xml")
        val ba = NetCdfBinaryArray("http://test.binary-array-ld.net/prefix.nc", netCdf)
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Prefix group not_prefix_list not found.", ise.message)
    }

    @Test
    fun prefixMapping_prefixGroupAttrNonString_throwsException() {
        val netCdf = convertToNetCdf("/netcdf/prefix-attr-error.xml")
        val ba = NetCdfBinaryArray("http://test.binary-array-ld.net/prefix.nc", netCdf)
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Global prefix attribute bald__isPrefixedBy has a non-string value.", ise.message)
    }

    @Test
    fun prefixMapping_toMap_prefixUriNonString_throwsException() {
        val netCdf = convertToNetCdf("/netcdf/prefix-uri-error.xml")
        val ba = NetCdfBinaryArray("http://test.binary-array-ld.net/prefix.nc", netCdf)
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping.toMap()
        }
        assertEquals("Prefix attribute skos__ has a non-string value.", ise.message)
    }
}