package net.bald.netcdf

import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Attribute
import ucar.nc2.AttributeContainer

/**
 * Class for building a
 * NetCDF implementation of [PrefixMapping].
 */
class NetCdfPrefixMappingBuilder(
    private val attrs: AttributeContainer
) {
    fun build(): PrefixMapping {
        val prefixMap = attrs.asSequence()
            .filter(::isPrefixAttr)
            .associateBy(::prefix, ::uri)
        return PrefixMapping.Factory.create().setNsPrefixes(prefixMap)
    }

    private fun isPrefixAttr(attr: Attribute): Boolean {
        return attr.shortName.endsWith(suffix)
    }

    private fun prefix(attr: Attribute): String {
        return attr.shortName.substringBeforeLast("__")
    }

    private fun uri(attr: Attribute): String {
        return attr.stringValue ?: throw IllegalStateException("Prefix attribute ${attr.shortName} must have a string value.")
    }

    companion object {
        private const val suffix = "__"
    }
}