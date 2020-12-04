package net.bald.netcdf

import bald.netcdf.CdlConverter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetCdfPathTest {
    private val uri = "http://test.binary-array-ld.net/example"
    private val ba: NetCdfBinaryArray = fromCdl()

    private fun fromCdl(): NetCdfBinaryArray {
        val file = CdlConverter.writeToNetCdf("/netcdf/paths.cdl")
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    private fun path(path: String): NetCdfPath {
        return NetCdfPath.parse(path)
    }

    @Test
    fun locateVar_absolutePath_fromRoot_resolvesPath() {
        val result = path("/foo/bar/var4").locateVar(ba.root)
        assertEquals("$uri/foo/bar/var4", result?.uri)
    }

    @Test
    fun locateVar_absolutePath_fromSubgroup_resolvesPath() {
        val result = path("/baz/var6").locateVar(ba.root.subContainer("foo")!!)
        assertEquals("$uri/baz/var6", result?.uri)
    }

    @Test
    fun locateVar_relativePath_fromRoot_resolvesPath() {
        val result = path("foo/bar/var4").locateVar(ba.root)
        assertEquals("$uri/foo/bar/var4", result?.uri)
    }

    @Test
    fun locateVar_relativePath_fromSubgroup_resolvesPath() {
        val result = path("bar/var4").locateVar(ba.root.subContainer("foo")!!)
        assertEquals("$uri/foo/bar/var4", result?.uri)

    }

    @Test
    fun locateVar_relativeParentPath_fromRoot_returnsNull() {
        val result = path("../foo/bar/var4").locateVar(ba.root)
        assertNull(result)
    }

    @Test
    fun locateVar_relativeParentPath_fromSubgroup_resolvesPath() {
        val result = path("../baz/var6").locateVar(ba.root.subContainer("foo")!!)
        assertEquals("$uri/baz/var6", result?.uri)
    }

    @Test
    fun locateVar_absolutePath_groupDoesNotExist_returnsNull() {
        val result = path("/group/var0").locateVar(ba.root)
        assertNull(result)
    }

    @Test
    fun locateVar_relativePath_groupDoesNotExist_returnsNull() {
        val result = path("group/var0").locateVar(ba.root)
        assertNull(result)
    }

    @Test
    fun locateVar_variableDoesNotExist_returnsNull() {
        val result = path("foo/bar/baz").locateVar(ba.root)
        assertNull(result)
    }
}