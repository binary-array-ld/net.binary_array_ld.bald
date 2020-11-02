package net.bald.netcdf

import net.bald.Attribute
import net.bald.Var
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Var].
 */
class NetCdfVar(
    private val v: Variable
): Var {
    override val name: String get() = v.shortName

    override fun attributes(prefixMapping: PrefixMapping): List<Attribute> {
        val source = v.attributes().let(::NetCdfAttributeSource)
        return source.attributes(prefixMapping)
    }

    override fun toString(): String {
        return v.toString()
    }
}