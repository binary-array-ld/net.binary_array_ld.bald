package net.bald.model

import net.bald.AttributeSource
import net.bald.Dimension
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral

open class ModelVarBuilder(
    private val container: Resource,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    open fun addVar(v: Var) {
        val vRes = container.model.createResource(v.uri, BALD.Resource)
        container.addProperty(BALD.contains, vRes)
        addAttributes(v, vRes)
        addDimensions(v, vRes)
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes().forEach(builder::addAttribute)
    }

    private fun addDimensions(v: Var, resource: Resource) {
        val valueIt = v.dimensions()
            .map(Dimension::size)
            .map(::createTypedLiteral)
            .iterator()

        if (valueIt.hasNext()) {
            val list = resource.model.createList(valueIt)
            resource.addProperty(BALD.shape, list)
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