package net.bald.netcdf

import net.bald.Container
import net.bald.alias.AliasDefinition
import ucar.nc2.Group

/**
 * NetCDF implementation of [Container] based on a named sub-group.
 */
class NetCdfSubContainer(
    override val parent: NetCdfContainer,
    private val group: Group
): NetCdfContainer(group) {
    override val uri: String get() = parent.childUri(group.shortName)
    override val root: NetCdfContainer get() = parent.root
    override val alias: AliasDefinition get() = parent.alias
    override val uriParser: UriParser get() = parent.uriParser

    override fun childUri(name: String): String {
        return "$uri/$name"
    }
}