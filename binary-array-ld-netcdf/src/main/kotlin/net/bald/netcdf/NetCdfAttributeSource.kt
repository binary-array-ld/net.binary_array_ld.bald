package net.bald.netcdf

import net.bald.AttributeSource
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Attribute
import ucar.nc2.AttributeContainer

/**
 * NetCDF implementation of [AttributeSource].
 */
class NetCdfAttributeSource(
    private val attrs: AttributeContainer
): AttributeSource {
    override fun attributes(prefixMapping: PrefixMapping): List<net.bald.Attribute> {
        val uriParser = UriParser(prefixMapping)
        return attrs.asSequence().filterNot(::isReserved).map { attr ->
            NetCdfAttribute(attr, uriParser)
        }.toList()
    }

    private fun isReserved(attr: Attribute): Boolean {
        return attr.shortName.startsWith("_")
    }
}