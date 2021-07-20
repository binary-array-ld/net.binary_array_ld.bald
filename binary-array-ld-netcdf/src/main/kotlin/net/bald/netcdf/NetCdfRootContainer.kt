package net.bald.netcdf

import net.bald.Container
import net.bald.alias.AliasDefinition
import ucar.nc2.Group
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Container] based on the root group.
 */
class NetCdfRootContainer(
    private val ba: NetCdfBinaryArray,
    group: Group,
): NetCdfContainer(group) {
    override val uri: String get() {
        val uri = ba.uri
        return if (uri.endsWith("/")) uri else "$uri/"
    }
    override val alias: AliasDefinition get() = ba.alias
    override val parent: NetCdfContainer? get() = null
    override val root: NetCdfContainer get() = this
    override val uriParser: UriParser get() = UriParser(ba.prefixMapping)

    private val prefixSrc = ba.prefixSrc

    override fun acceptGroup(group: Group): Boolean {
        return prefixSrc != group.shortName
    }

    override fun acceptVar(v: Variable): Boolean {
        return prefixSrc != v.shortName
    }

    override fun childUri(name: String): String {
        return uri + name
    }
}