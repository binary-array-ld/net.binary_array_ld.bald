package net.bald.netcdf

import net.bald.Attribute
import net.bald.Container
import net.bald.Var
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Group

/**
 * NetCDF implementation of [Container].
 */
class NetCdfContainer(
    private val group: Group
): Container {
    override fun vars(): Sequence<Var> {
        return group.variables.asSequence().map(::NetCdfVar)
    }

    override fun attributes(prefixMapping: PrefixMapping): List<Attribute> {
        val uriParser = UriParser(prefixMapping)
        return group.attributes().map { attr ->
            NetCdfAttribute(attr, uriParser)
        }
    }
}