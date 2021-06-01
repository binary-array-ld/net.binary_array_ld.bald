package net.bald.netcdf

import ucar.nc2.Dimension

/**
 * NetCDF implementation of [net.bald.Dimension].
 */
class NetCdfDimension(
    private val dim: Dimension
): net.bald.Dimension {
    override val name: String get() = dim.fullName
    override val size: Int get() = dim.length

    val shortName get() = dim.shortName
}