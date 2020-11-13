package net.bald.model

import net.bald.Attribute
import org.apache.jena.rdf.model.Resource

open class ModelAttributeBuilder(
    private val resource: Resource
) {
    open fun addAttribute(attr: Attribute) {
        val propUri = attr.uri ?: resource.withTrailingSlash() + attr.name
        val prop = resource.model.createProperty(propUri)
        attr.values.forEach { value ->
            resource.addProperty(prop, value)
        }
    }

    open class Factory {
        open fun forResource(resource: Resource): ModelAttributeBuilder {
            return ModelAttributeBuilder(resource)
        }
    }
}