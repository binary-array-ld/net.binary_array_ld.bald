package net.bald.netcdf

import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral
import org.apache.jena.rdf.model.ResourceFactory.createResource
import ucar.nc2.Attribute

/**
 * NetCDF implementation of [net.bald.Attribute].
 */
class NetCdfAttribute(
    private val attr: Attribute,
    private val uriParser: UriParser
): net.bald.Attribute {
    override val name: String get() = attr.shortName

    override val uri: String? get() {
        return uriParser.parse(name)
    }

    override val values: List<RDFNode> get() {
        return if (attr.isArray) {
            attr.values?.let { values ->
                ArrayIterator(values).asSequence().map(::node).toList()
            }
        } else {
            attr.getValue(0)?.let(::node)?.let(::listOf)
        } ?: emptyList()
    }

    private fun node(value: Any): RDFNode {
        val str = value.toString()
        val uri = uriParser.parse(str)
        return if (uri != null) {
            createResource(uri)
        } else {
            createPlainLiteral(str)
        }
    }

    override fun toString(): String {
        return attr.toString()
    }
}