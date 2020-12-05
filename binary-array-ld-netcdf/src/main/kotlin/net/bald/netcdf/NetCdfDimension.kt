package net.bald.netcdf

import ucar.nc2.Dimension

/**
 * TODO
 */
class NetCdfDimension(
    private val dim: Dimension
): net.bald.Dimension {
    override val size: Int get() = dim.length
}