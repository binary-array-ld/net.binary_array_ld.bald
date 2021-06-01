package net.bald.netcdf

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory

interface ReferenceCollection {
    fun asVars(): Collection<NetCdfVar>
    fun asResources(): List<Resource>

    class Unordered(
        private val vars: List<NetCdfVar>
    ): ReferenceCollection {
        override fun asVars(): Collection<NetCdfVar> {
            return vars
        }

        override fun asResources(): List<Resource> {
            return vars.asSequence()
                .map(NetCdfVar::uri)
                .map(ResourceFactory::createResource)
                .toList()
        }
    }

    class Ordered(
        private val vars: List<NetCdfVar>
    ): ReferenceCollection {
        override fun asVars(): Collection<NetCdfVar> {
            return vars
        }

        override fun asResources(): List<Resource> {
            val nodeIt = vars.asSequence()
                .map(NetCdfVar::uri)
                .map(ResourceFactory::createResource)
                .iterator()

            return ModelFactory.createDefaultModel()
                .createList(nodeIt)
                .let(::listOf)
        }
    }
}