package net.bald.netcdf

import net.bald.Attribute
import net.bald.Container
import net.bald.Var
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Group
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Container].
 */
class NetCdfContainer(
    private val group: Group,
    private val prefixSrc: String? = null
): Container {
    override val name: String? get() = group.shortName

    override fun vars(): Sequence<Var> {
        return group.variables.asSequence().filter(::acceptVar).map(::NetCdfVar)
    }

    override fun subContainers(): Sequence<Container> {
        return group.groups.asSequence().filter(::acceptGroup).map(::NetCdfContainer)
    }

    private fun acceptVar(v: Variable): Boolean {
        return prefixSrc != v.shortName
    }

    private fun acceptGroup(group: Group): Boolean {
        return prefixSrc != group.shortName
    }

    override fun attributes(prefixMapping: PrefixMapping): List<Attribute> {
        val source = group.attributes().let(::NetCdfAttributeSource)
        return source.attributes(prefixMapping)
    }
}
