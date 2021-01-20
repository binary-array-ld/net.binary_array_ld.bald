package net.bald.model

import net.bald.AttributeSource
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Literal
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral

open class ModelVarBuilder(
    private val container: Resource,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    open fun addVar(v: Var) {
        val dimBuilder = dimensionBuilder(v)
        val vRes = container.model.createResource(v.uri, dimBuilder.type)
        container.addProperty(BALD.contains, vRes)
        addAttributes(v, vRes)
        addCoordinateRange(v, vRes)
        dimBuilder.addDimensions(vRes)
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes().forEach(builder::addAttribute)
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
        val valueIt = v.dimensions()
            .map(Dimension::size)
            .map(::createTypedLiteral)
            .iterator()

        return if (valueIt.hasNext()) {
            VarDimensionBuilder.Dimensional(valueIt)
        } else {
            VarDimensionBuilder.Base
        }
    }

    private interface VarDimensionBuilder {
        val type: Resource
        fun addDimensions(resource: Resource)

        object Base: VarDimensionBuilder {
            override val type: Resource get() = BALD.Resource

            override fun addDimensions(resource: Resource) {
                // do nothing
            }
        }

        class Dimensional(
            private val valueIt: Iterator<Literal>
        ): VarDimensionBuilder {
            override val type: Resource get() = BALD.Array

            override fun addDimensions(resource: Resource) {
                val list = resource.model.createList(valueIt)
                resource.addProperty(BALD.shape, list)
            }
        }
    }

    open class Factory(
        private val attrFct: ModelAttributeBuilder.Factory
    ) {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container, attrFct)
        }
    }
}