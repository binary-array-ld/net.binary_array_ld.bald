package net.bald.netcdf

import net.bald.AttributeSource
import ucar.nc2.Attribute
import ucar.nc2.AttributeContainer

/**
 * NetCDF implementation of [AttributeSource].
 */
class NetCdfAttributeSource(
    private val parent: NetCdfContainer,
    private val attrs: AttributeContainer
): AttributeSource {

    override fun attributes(): Sequence<NetCdfAttribute> {
        return attrs.asSequence().filterNot(::isReserved).map { attr ->
            NetCdfAttribute(parent, attr)
        }
    }

    private fun isReserved(attr: Attribute): Boolean {
        return attr.shortName.startsWith("_")
    }
}