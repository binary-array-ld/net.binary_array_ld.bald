package net.bald.netcdf

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory

class ReferenceValueParser(
    private val group: NetCdfContainer
) {
    fun parse(value: String): List<Resource>? {
        return value.trim().let(::doParse)
    }

    private fun doParse(value: String): List<Resource>? {
        val collector = if (value.startsWith('(') && value.endsWith(')')) {
            value.substringAfter('(').substringBeforeLast(')').let(ReferenceCollector::Ordered)
        } else {
            ReferenceCollector.Unordered(value)
        }

        val nodes = split(collector.raw).map(::resolve).toList()
        val referenceNodes = nodes.filterNotNull()

        return if (referenceNodes.size != nodes.size) null else {
            collector.collect(referenceNodes)
        }
    }

    private fun split(values: String): Sequence<String> {
        return values.splitToSequence(' ')
            .filterNot(String::isEmpty)
            .map(String::trim)
    }

    private fun resolve(value: String): Resource? {
        return NetCdfPath.parse(value).locateVar(group)?.uri?.let(ResourceFactory::createResource)
    }

    private interface ReferenceCollector {
        val raw: String
        fun collect(nodes: List<Resource>): List<Resource>

        class Unordered(
            override val raw: String
        ): ReferenceCollector {
            override fun collect(nodes: List<Resource>): List<Resource> {
                return nodes
            }

        }

        class Ordered(
            override val raw: String
        ): ReferenceCollector {
            override fun collect(nodes: List<Resource>): List<Resource> {
                val nodeIt = nodes.iterator()
                return ModelFactory.createDefaultModel().createList(nodeIt).let(::listOf)
            }
        }
    }
}