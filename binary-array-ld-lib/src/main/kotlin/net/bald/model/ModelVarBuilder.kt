package net.bald.model

import net.bald.AttributeSource
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.RDFList
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral
import org.apache.jena.vocabulary.RDF

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
        val dims = v.dimensions().toList()
        return if (dims.isEmpty()) {
            VarDimensionBuilder.Base
        } else {
            VarDimensionBuilder.Dimensional(container.model, dims)
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
            private val model: Model,
            private val dims: List<Dimension>
        ): VarDimensionBuilder {
            override val type: Resource get() = BALD.Array

            override fun addDimensions(resource: Resource) {
                val model = resource.model
                val shape = shape()
                resource.addProperty(BALD.shape, shape)

                dims.forEachIndexed { idx, dim ->
                    dim.coordinate?.let { coordinate ->
                        if (coordinate.uri != resource.uri) {
                            val target = model.createResource(coordinate.uri)
                            val targetRefShape = targetRefShape(dim, idx)

                            val reference = model.createResource()
                                .addProperty(RDF.type, BALD.Reference)
                                .addProperty(BALD.targetRefShape, targetRefShape)
                                .addProperty(BALD.target, target)
                            resource.addProperty(BALD.references, reference)
                        }
                    }
                }
            }

            private fun shape(): RDFList {
                val sizeIt = dims.map(::size).iterator()
                return model.createList(sizeIt)
            }

            private fun targetRefShape(dim: Dimension, ordinal: Int): RDFList {
                val nodeIt = (dims.indices).map { idx ->
                    if (idx == ordinal) {
                        size(dim)
                    } else unitNode
                }.iterator()

                return model.createList(nodeIt)
            }

            private fun size(dim: Dimension): RDFNode {
                return createTypedLiteral(dim.size)
            }
        }
    }

    companion object {
        private val unitNode = createTypedLiteral(1)
    }

    open class Factory(
        private val attrFct: ModelAttributeBuilder.Factory
    ) {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container, attrFct)
        }
    }
}