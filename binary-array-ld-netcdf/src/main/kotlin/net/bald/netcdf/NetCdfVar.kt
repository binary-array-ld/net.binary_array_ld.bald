package net.bald.netcdf

import net.bald.CoordinateRange
import net.bald.Var
import net.bald.vocab.BALD
import ucar.nc2.AttributeContainer
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Var].
 */
class NetCdfVar(
    private val parent: NetCdfContainer,
    private val v: Variable
): Var {
    val shortName: String get() = v.shortName
    val name: String get() = v.fullName
    override val uri: String get() = parent.childUri(shortName)

    fun isCoordinate(): Boolean {
        return v.isCoordinateVariable
    }

    override val range: CoordinateRange? get() {
        return if (isCoordinate()) {
            NetCdfCoordinateRange(v)
        } else null
    }

    override fun attributes(): Sequence<NetCdfAttribute> {
        return v.attributes()
            .let(::source)
            .attributes()
    }

    private fun source(attrs: AttributeContainer): NetCdfAttributeSource {
        return NetCdfAttributeSource(parent, attrs)
    }

    override fun dimensions(): Sequence<NetCdfDimension> {
        return v.dimensions.asSequence().map(::dimension)
    }

    private fun dimension(dim: ucar.nc2.Dimension): NetCdfDimension {
        return NetCdfDimension(dim)
    }

    override fun references(): Sequence<Var> {
        val coordinates = coordinateRefs()
        val attrs = attributeRefs()

        return (coordinates + attrs).distinctBy(NetCdfVar::uri)
    }

    private fun coordinateRefs(): Sequence<NetCdfVar> {
        val dims = dimensions().toList()
        return if (dims.isNotEmpty()) {
            val coordinatesByName = parent.vars()
                .filterNot { v -> v.name == name }
                .filter(NetCdfVar::isCoordinate)
                .associateBy { v -> v.shortName }

            dims.asSequence()
                .map(NetCdfDimension::shortName)
                .mapNotNull(coordinatesByName::get)
        } else emptySequence()
    }

    private fun attributeRefs(): Sequence<NetCdfVar> {
        return v.attributes().let(::source)
            .attributes()
            .mapNotNull(NetCdfAttribute::rawValues)
            .flatten()
            .mapNotNull(parent::parseReferences)
            .flatMap(ReferenceCollection::asVars)
    }

    override fun toString(): String {
        return v.toString()
    }
}