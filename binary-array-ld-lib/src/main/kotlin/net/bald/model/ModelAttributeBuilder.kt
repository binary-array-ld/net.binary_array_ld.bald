package net.bald.model

import net.bald.Attribute
import org.apache.jena.rdf.model.RDFList
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

open class ModelAttributeBuilder(
    private val resource: Resource
) {
    open fun addAttribute(attr: Attribute) {
        val prop = resource.model.createProperty(attr.uri)
        attr.values.asSequence().map(::inModel).forEach { value ->
            resource.addProperty(prop, value)
        }
    }

    private fun inModel(node: RDFNode): RDFNode {
        return if (node is RDFList) {
            // copy the list from its original model to the model being built
            node.iterator().let(resource.model::createList)
        } else {
            node
        }
    }

    open class Factory {
        open fun forResource(resource: Resource): ModelAttributeBuilder {
            return ModelAttributeBuilder(resource)
        }
    }
}