package net.bald.netcdf

import net.bald.Distribution

/**
 * NetCDF implementation of [Distribution].
 */
class NetCdfDistribution: Distribution {
    override val mediaType: String get() = "application/x-netcdf"
}