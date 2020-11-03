package net.bald.model

import net.bald.AttributeSource
import net.bald.vocab.BALD
import net.bald.Container
import org.apache.jena.rdf.model.Resource

open class ModelContainerBuilder(
    private val parent: Resource,
    private val varFct: ModelVarBuilder.Factory,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    open fun addContainer(container: Container) {
        val containerUri = containerUri(container)
        val containerRes = parent.model.createResource(containerUri, BALD.Container)
        addSubContainers(container, containerRes)
        addVars(container, containerRes)
        addAttributes(container, containerRes)
        parent.addProperty(BALD.contains, containerRes)
    }

    private fun containerUri(container: Container): String {
        val parentUri = parent.uri
        val prefix = if (parentUri.endsWith('/')) parentUri else "$parentUri/"
        return prefix + (container.name ?: "")
    }

    private fun addSubContainers(container: Container, containerRes: Resource) {
        val builder = ModelContainerBuilder(containerRes, varFct, attrFct)
        container.subContainers().forEach(builder::addContainer)
    }

    private fun addVars(container: Container, containerRes: Resource) {
        varFct.forContainer(containerRes).apply {
            container.vars().forEach(::addVar)
        }
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes(resource.model).forEach { attr ->
            builder.addAttribute(attr)
        }
    }

    open class Factory(
        private val varFct: ModelVarBuilder.Factory,
        private val attrFct: ModelAttributeBuilder.Factory
    ) {
        open fun forParent(parent: Resource): ModelContainerBuilder {
            return ModelContainerBuilder(parent, varFct, attrFct)
        }
    }
}




