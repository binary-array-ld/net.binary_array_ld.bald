package net.bald.model

import net.bald.AttributeSource
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.*
import org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral

open class ModelVarBuilder(
    private val container: Resource,
    private val attrFct: ModelAttributeBuilder.Factory,
    private val refFct: ModelReferenceBuilder.Factory
) {
    open fun addVar(v: Var) {
        val dimBuilder = dimensionBuilder(v)
        val vRes = container.model.createResource(v.uri, dimBuilder.type)
        container.addProperty(BALD.contains, vRes)
        addAttributes(v, vRes)
        addCoordinateRange(v, vRes)
        dimBuilder.addShape(vRes)
        dimBuilder.addReferences(vRes)
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes().filterNot { attr ->
            BALD.references.hasURI(attr.uri)
        }.forEach(builder::addAttribute)
    }

    private fun addCoordinateRange(v: Var, resource: Resource) {
        v.range?.let { range ->
            range.first?.let(::createTypedLiteral)?.let { first ->
                resource.addProperty(BALD.arrayFirstValue, first)
            }
            range.last?.let(::createTypedLiteral)?.let { last ->
                resource.addProperty(BALD.arrayLastValue, last)
            }
        }
    }

    private fun dimensionBuilder(v: Var): VarDimensionBuilder {
        val dims = v.dimensions().toList()
        return if (dims.isEmpty()) {
            VarDimensionBuilder.BaldResource
        } else {
            BaldArray(v, dims)
        }
    }

    private interface VarDimensionBuilder {
        val type: Resource
        fun addShape(resource: Resource)
        fun addReferences(resource: Resource)

        object BaldResource: VarDimensionBuilder {
            override val type: Resource get() = BALD.Resource

            override fun addShape(resource: Resource) {
                // do nothing
            }

            override fun addReferences(resource: Resource) {
                // do nothing
            }
        }
    }

    private inner class BaldArray(
        private val v: Var,
        private val dims: List<Dimension>,
    ): VarDimensionBuilder {
        override val type: Resource get() = BALD.Array

        override fun addShape(resource: Resource) {
            val shape = dims.map(::size).iterator().let(resource.model::createList)
            resource.addProperty(BALD.shape, shape)
        }

        private fun size(dim: Dimension): RDFNode {
            return createTypedLiteral(dim.size)
        }

        override fun addReferences(resource: Resource) {
            val builder = refFct.forVar(v, resource)
            v.references().forEach(builder::addReference)
        }
    }

    open class Factory(
        private val attrFct: ModelAttributeBuilder.Factory,
        private val refFct: ModelReferenceBuilder.Factory
    ) {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container, attrFct, refFct)
        }
    }
}