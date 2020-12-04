package net.bald.netcdf

import net.bald.Attribute
import net.bald.Var
import ucar.nc2.AttributeContainer
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Var].
 */
class NetCdfVar(
    private val parent: NetCdfContainer,
    private val v: Variable
): Var {
    override val uri: String get() = parent.childUri(v.shortName)

    override fun attributes(): List<Attribute> {
        return v.attributes()
            .let(::source)
            .attributes()
    }

    private fun source(attrs: AttributeContainer): NetCdfAttributeSource {
        return NetCdfAttributeSource(parent, attrs)
    }

    override fun toString(): String {
        return v.toString()
    }
}