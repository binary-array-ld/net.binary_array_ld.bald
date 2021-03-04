package net.bald.netcdf

import net.bald.Var
import ucar.nc2.Dimension

/**
 * NetCDF implementation of [net.bald.Dimension].
 */
class NetCdfDimension(
    private val parent: NetCdfContainer,
    private val dim: Dimension
): net.bald.Dimension {
    override val size: Int get() = dim.length

    override val coordinate: Var? get() {
        return parent.vars().find { v ->
            v.isCoordinate() && v.name == dim.shortName
        }
    }
}