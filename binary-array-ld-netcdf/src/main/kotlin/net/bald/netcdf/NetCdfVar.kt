package net.bald.netcdf

import net.bald.Attribute
import net.bald.CoordinateRange
import net.bald.Dimension
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
    val name: String get() = v.shortName
    override val uri: String get() = parent.childUri(name)

    fun isCoordinate(): Boolean {
        return v.isCoordinateVariable
    }

    override val range: CoordinateRange? get() {
        return if (isCoordinate()) {
            NetCdfCoordinateRange(v)
        } else null
    }

    override fun attributes(): List<Attribute> {
        return v.attributes().let(::source).attributes()
    }

    private fun source(attrs: AttributeContainer): NetCdfAttributeSource {
        return NetCdfAttributeSource(parent, attrs)
    }

    override fun dimensions(): Sequence<Dimension> {
        return v.dimensions.asSequence().map(::dimension)
    }

    private fun dimension(dim: ucar.nc2.Dimension): Dimension {
        return NetCdfDimension(parent, dim)
    }

    override fun toString(): String {
        return v.toString()
    }
}