package net.bald.netcdf

import bald.netcdf.CdlConverter.convertToNetCdf
import net.bald.BinaryArray
import net.bald.Container
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NetCdfBinaryArrayTest {
    private fun fromCdl(cdlLoc: String, uri: String? = null): BinaryArray {
        val file = convertToNetCdf(cdlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    @Test
    fun create_withUri_returnsBinaryArray() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)
        assertEquals(uri, ba.uri)
    }

    @Test
    fun create_withoutUri_returnsBinaryArrayWithFileUri() {
        val netCdfFile = convertToNetCdf("/netcdf/identity.cdl")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        val expectedUri = netCdfFile.toPath().toUri().toString()
        assertEquals(expectedUri, ba.uri)
    }

    @Test
    fun create_withVars_returnsBinaryArrayWithVars() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)

        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("var0", vars[0].name)
        assertEquals("var1", vars[1].name)
    }

    @Test
    fun create_withSubgroups_returnsSubgroups() {
        val uri = "http://test.binary-array-ld.net/identity-subgroups.nc"
        val ba = fromCdl("/netcdf/identity-subgroups.cdl", uri)
        val root = ba.root
        assertEquals("", root.name)
        val groups = root.subContainers().sortedBy(Container::name).toList()
        assertEquals(2, groups.size)

        val group0 = groups[0]
        val group0Vars = group0.vars().toList()
        assertEquals("group0", group0.name)
        assertEquals(2, group0Vars.size)
        assertEquals("var2", group0Vars[0].name)
        assertEquals("var3", group0Vars[1].name)

        val group1 = groups[1]
        val group1Vars = group1.vars().toList()
        assertEquals("group1", group1.name)
        assertEquals(2, group1Vars.size)
        assertEquals("var4", group1Vars[0].name)
        assertEquals("var5", group1Vars[1].name)
    }
}