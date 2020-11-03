package net.bald.model

import net.bald.vocab.BALD
import net.bald.Container
import org.apache.jena.rdf.model.Resource

open class ModelContainerBuilder(
    private val parent: Resource,
    private val varFct: ModelVarBuilder.Factory
) {
    open fun addContainer(container: Container) {
        val containerUri = containerUri(container)
        val containerRes = parent.model.createResource(containerUri, BALD.Container)
        buildSubgroups(container, containerRes)
        buildVars(container, containerRes)
        parent.addProperty(BALD.contains, containerRes)
    }

    private fun containerUri(container: Container): String {
        val parentUri = parent.uri
        val prefix = if (parentUri.endsWith('/')) parentUri else "$parentUri/"
        return prefix + (container.name ?: "")
    }

    private fun buildSubgroups(container: Container, containerRes: Resource) {
        val builder = ModelContainerBuilder(containerRes, varFct)
        container.subContainers().forEach(builder::addContainer)
    }

    private fun buildVars(container: Container, containerRes: Resource) {
        varFct.forContainer(containerRes).apply {
            container.vars().forEach(::addVar)
        }
    }

    open class Factory(
        private val varFct: ModelVarBuilder.Factory
    ) {
        open fun forParent(parent: Resource): ModelContainerBuilder {
            return ModelContainerBuilder(parent, varFct)
        }
    }
}




