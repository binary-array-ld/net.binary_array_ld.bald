package net.bald.netcdf

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.RDFNode
import ucar.nc2.Attribute

/**
 * NetCDF implementation of [net.bald.Attribute].
 */
class NetCdfAttribute(
    private val parent: NetCdfContainer,
    private val attr: Attribute
): net.bald.Attribute {
    private val name: String get() = attr.shortName
    private val prop: Property get() = parent.parseProperty(name)

    override val uri: String get() = prop.uri

    override val values: Sequence<RDFNode> get() {
        return rawValues()?.flatMap(::nodes) ?: emptySequence()
    }

    fun rawValues(): Sequence<String>? {
        return if (attr.isArray) {
            attr.values?.let { values ->
                ArrayIterator(values).asSequence().map(Any::toString)
            }
        } else {
            attr.getValue(0)?.toString()?.let { value ->
                sequenceOf(value)
            }
        }
    }

    private fun nodes(value: String): List<RDFNode> {
        return parent.parseRdfNodes(prop, value)
    }

    override fun toString(): String {
        return attr.toString()
    }
}