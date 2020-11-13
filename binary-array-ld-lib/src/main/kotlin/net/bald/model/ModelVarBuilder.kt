package net.bald.model

import net.bald.AttributeSource
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Resource

open class ModelVarBuilder(
    private val container: Resource,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    open fun addVar(v: Var) {
        val varUri = container.withTrailingSlash() + v.name
        val vRes = container.model.createResource(varUri, BALD.Resource)
        container.addProperty(BALD.contains, vRes)
        addAttributes(v, vRes)
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes(resource.model).forEach(builder::addAttribute)
    }

    open class Factory(
        private val attrFct: ModelAttributeBuilder.Factory
    ) {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container, attrFct)
        }
    }
}