package net.bald.netcdf

import net.bald.Distribution
import org.apache.jena.rdf.model.Resource

/**
 * NetCDF implementation of [Distribution].
 */
class NetCdfDistribution(
    override val downloadUrl: Resource? = null
): Distribution {
    override val mediaType: String get() = "application/x-netcdf"
}