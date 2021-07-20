package net.bald.model

import net.bald.AttributeSource
import net.bald.Container
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource

open class ModelContainerBuilder(
    private val model: Model,
    private val parent: Resource?,
    private val varFct: ModelVarBuilder.Factory,
    private val attrFct: ModelAttributeBuilder.Factory
) {
    constructor(
        parent: Resource,
        varFct: ModelVarBuilder.Factory,
        attrFct: ModelAttributeBuilder.Factory
    ): this(parent.model, parent, varFct, attrFct)

    open fun addContainer(container: Container) {
        val containerRes = model.createResource(container.uri, BALD.Container)
        addSubContainers(container, containerRes)
        addVars(container, containerRes)
        addAttributes(container, containerRes)
        parent?.addProperty(BALD.contains, containerRes)
    }

    private fun addSubContainers(container: Container, containerRes: Resource) {
        val builder = ModelContainerBuilder(containerRes, varFct, attrFct)
        container.subContainers().forEach(builder::addContainer)
    }

    private fun addVars(container: Container, containerRes: Resource) {
        val builder = varFct.forContainer(containerRes)
        container.vars().forEach(builder::addVar)
    }

    private fun addAttributes(source: AttributeSource, resource: Resource) {
        val builder = attrFct.forResource(resource)
        source.attributes().forEach(builder::addAttribute)
    }

    open class Factory(
        private val varFct: ModelVarBuilder.Factory,
        private val attrFct: ModelAttributeBuilder.Factory
    ) {
        open fun forParent(parent: Resource): ModelContainerBuilder {
            return ModelContainerBuilder(parent, varFct, attrFct)
        }

        open fun forRoot(model: Model): ModelContainerBuilder {
            return ModelContainerBuilder(model, parent = null, varFct, attrFct)
        }
    }
}




