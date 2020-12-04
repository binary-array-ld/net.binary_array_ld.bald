package net.bald.netcdf

import net.bald.Attribute
import net.bald.Container
import net.bald.Var
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.Group
import ucar.nc2.Variable

/**
 * NetCDF implementation of [Container].
 * See [NetCdfRootContainer] for the root group representation,
 * and [NetCdfSubContainer] for sub-groups.
 */
abstract class NetCdfContainer(
    private val group: Group
): Container {

    override fun vars(): Sequence<Var> {
        return group.variables.asSequence().filter(::acceptVar).map(::toVar)
    }

    override fun subContainers(): Sequence<Container> {
        return group.groups.asSequence().filter(::acceptGroup).map(::subContainer)
    }

    private fun toVar(v: Variable): Var {
        return NetCdfVar(this, v)
    }

    private fun subContainer(group: Group): Container {
        return NetCdfSubContainer(this, group)
    }

    open fun acceptVar(v: Variable): Boolean {
        return true
    }

    open fun acceptGroup(group: Group): Boolean {
        return true
    }

    override fun attributes(prefixMapping: PrefixMapping): List<Attribute> {
        val source = group.attributes().let(::NetCdfAttributeSource)
        return source.attributes(prefixMapping)
    }

    abstract fun childUri(name: String): String
}