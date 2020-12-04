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
    abstract val parent: NetCdfContainer?
    abstract val root: NetCdfContainer
    abstract fun childUri(name: String): String

    override fun vars(): Sequence<Var> {
        return group.variables.asSequence().filter(::acceptVar).map(::variable)
    }

    override fun subContainers(): Sequence<Container> {
        return group.groups.asSequence().filter(::acceptGroup).map(::subContainer)
    }

    private fun variable(v: Variable): NetCdfVar {
        return NetCdfVar(this, v)
    }

    private fun subContainer(group: Group): NetCdfContainer {
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

    fun subContainer(name: String): NetCdfContainer? {
        return group.findGroup(name)?.let(::subContainer)
    }

    fun variable(name: String): NetCdfVar? {
        return group.findVariable(name)?.let(::variable)
    }
}