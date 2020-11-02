package net.bald.netcdf

import net.bald.Container
import net.bald.Var
import ucar.nc2.Group

/**
 * NetCDF implementation of [Container].
 */
class NetCdfContainer(
    private val group: Group
): Container {
    override val name: String? get() = group.shortName

    override fun vars(): Sequence<Var> {
        return group.variables.asSequence().map(::NetCdfVar)
    }

    override fun subContainers(): Sequence<Container> {
        return group.groups.asSequence().map(::NetCdfContainer)
    }
}