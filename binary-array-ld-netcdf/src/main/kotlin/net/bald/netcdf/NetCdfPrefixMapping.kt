package net.bald.netcdf

import net.bald.PrefixMapping
import ucar.nc2.Attribute
import ucar.nc2.AttributeContainer

/**
 * NetCDF implementation of [PrefixMapping].
 */
class NetCdfPrefixMapping(
    private val attrs: AttributeContainer
): PrefixMapping {
    override fun toMap(): Map<String, String> {
        return attrs.asSequence()
            .filter(::isPrefixAttr)
            .associateBy(::prefix, ::uri)
    }

    private fun isPrefixAttr(attr: Attribute): Boolean {
        return attr.shortName.endsWith(suffix)
    }

    private fun prefix(attr: Attribute): String {
        return attr.shortName.substringBeforeLast("__")
    }

    private fun uri(attr: Attribute): String {
        return attr.stringValue ?: throw IllegalStateException("Prefix attribute ${attr.shortName} has a non-string value.")
    }

    companion object {
        private const val suffix = "__"
    }
}