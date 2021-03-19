package net.bald.netcdf

import net.bald.Attribute
import net.bald.Container
import net.bald.Var
import net.bald.alias.AliasDefinition
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral
import org.apache.jena.rdf.model.ResourceFactory.createResource
import ucar.nc2.AttributeContainer
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
    abstract val alias: AliasDefinition
    abstract val uriParser: UriParser
    abstract fun childUri(name: String): String

    private val refParser: ReferenceValueParser get() {
        return ReferenceValueParser(this)
    }

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

    override fun attributes(): List<Attribute> {
        return group.attributes().let(::source).attributes()
    }

    fun subContainer(name: String): NetCdfContainer? {
        return group.findGroup(name)?.let(::subContainer)
    }

    fun variable(name: String): NetCdfVar? {
        return group.findVariable(name)?.let(::variable)
    }

    private fun source(attrs: AttributeContainer): NetCdfAttributeSource {
        return NetCdfAttributeSource(this, attrs)
    }

    fun parseProperty(name: String): Property {
        return uriParser.parse(name)?.let(ResourceFactory::createProperty)
            ?: alias.property(name)
            ?: childUri(name).let(ResourceFactory::createProperty)
    }

    fun parseRdfNodes(prop: Property, value: String): List<RDFNode> {
        return uriParser.parse(value)?.let(::createResource)?.let(::listOf)
            ?: alias.resource(value)?.let(::listOf)
            ?: prop.takeIf(alias::isReferenceProperty)?.let {
                refParser.parse(value)
            }
            ?: createPlainLiteral(value).let(::listOf)
    }

    override fun toString(): String {
        return group.toString()
    }
}