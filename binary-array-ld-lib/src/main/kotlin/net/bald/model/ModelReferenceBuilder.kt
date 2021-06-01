package net.bald.model

import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Literal
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.vocabulary.RDF

class ModelReferenceBuilder(
    v: Var,
    private val vRes: Resource
) {
    private val sourceShape = Shape(v)
    private val model = vRes.model

    fun addReference(target: Var) {
        val targetShape = Shape(target)
        val dimIds = (sourceShape.dims + targetShape.dims).map(Dimension::name).distinct()
        val sourceRefShape = sourceShape.shape(dimIds).let(model::createList)
        val targetRefShape = targetShape.shape(dimIds).let(model::createList)
        val targetRes = target.uri.let(model::createResource)

        val reference = model.createResource()
            .addProperty(RDF.type, BALD.Reference)
            .addProperty(BALD.sourceRefShape, sourceRefShape)
            .addProperty(BALD.targetRefShape, targetRefShape)
            .addProperty(BALD.target, targetRes)

        vRes.addProperty(BALD.references, reference)
    }

    class Shape(
        val dims: List<Dimension>
    ) {
        constructor(v: Var): this(v.dimensions().toList())

        private val dimsById = dims.associateBy(Dimension::name)

        fun shape(dimIds: List<String>): Iterator<Literal> {
            return dimIds.asSequence().map { dimId ->
                dimsById[dimId]?.size?.let(ResourceFactory::createTypedLiteral) ?: unitNode
            }.iterator()
        }
    }

    companion object {
        private val unitNode = ResourceFactory.createTypedLiteral(1)
    }

    open class Factory {
        open fun forVar(v: Var, vRes: Resource): ModelReferenceBuilder {
            return ModelReferenceBuilder(v, vRes)
        }
    }
}