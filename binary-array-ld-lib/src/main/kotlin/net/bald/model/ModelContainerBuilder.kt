package net.bald.model

import net.bald.AttributeSource
import net.bald.vocab.BALD
import net.bald.Container
import org.apache.jena.rdf.model.Resource

open class ModelContainerBuilder(
    private val ba: Resource,
    private val varFct: ModelVarBuilder.Factory,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    open fun addContainer(container: Container) {
        val rootUri = ba.uri + '/'
        val containerRes = ba.model.createResource(rootUri, BALD.Container)
        addVars(container, containerRes)
        addAttributes(container, containerRes)

        ba.addProperty(BALD.contains, containerRes)
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
        open fun forBinaryArray(ba: Resource): ModelContainerBuilder {
            return ModelContainerBuilder(ba, varFct, attrFct)
        }
    }
}




