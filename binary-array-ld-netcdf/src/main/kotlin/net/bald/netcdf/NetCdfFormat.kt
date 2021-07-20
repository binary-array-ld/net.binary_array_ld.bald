package net.bald.netcdf

import net.bald.Format
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory

/**
 * NetCDF implementation of [Format].
 */
object NetCdfFormat: Format {
    override val identifier: Resource
        get() = ResourceFactory.createResource("http://vocab.nerc.ac.uk/collection/M01/current/NC/")
}