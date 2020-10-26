package net.bald.netcdf

import bald.netcdf.NcmlConverter.convertToNetCdf
import net.bald.BinaryArray
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NetCdfBinaryArrayTest {
    private fun fromNcml(ncmlLoc: String, uri: String? = null): BinaryArray {
        val file = convertToNetCdf(ncmlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    @Test
    fun create_withUri_returnsBinaryArray() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromNcml("/netcdf/identity.xml", uri)
        assertEquals(uri, ba.uri)
    }

    @Test
    fun create_withoutUri_returnsBinaryArrayWithFileUri() {
        val netCdfFile = convertToNetCdf("/netcdf/identity.xml")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        val expectedUri = netCdfFile.toPath().toUri().toString()
        assertEquals(expectedUri, ba.uri)
    }

    @Test
    fun create_withVars_returnsBinaryArrayWithVars() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromNcml("/netcdf/identity.xml", uri)

        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("var0", vars[0].name)
        assertEquals("var1", vars[1].name)
    }
}