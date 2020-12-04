package net.bald.netcdf

import net.bald.Container
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

    override fun childUri(name: String): String {
        return "$uri/$name"
    }
}