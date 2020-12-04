package net.bald.netcdf

import net.bald.AttributeSource
import net.bald.context.AliasDefinition
import org.apache.jena.rdf.model.Property
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Attribute
import ucar.nc2.AttributeContainer

/**
 * NetCDF implementation of [AttributeSource].
 */
class NetCdfAttributeSource(
    private val parent: NetCdfContainer,
    private val attrs: AttributeContainer
): AttributeSource {

    override fun attributes(): List<net.bald.Attribute> {
        return attrs.asSequence().filterNot(::isReserved).map { attr ->
            NetCdfAttribute(parent, attr)
        }.toList()
    }

    private fun isReserved(attr: Attribute): Boolean {
        return attr.shortName.startsWith("_")
    }
}