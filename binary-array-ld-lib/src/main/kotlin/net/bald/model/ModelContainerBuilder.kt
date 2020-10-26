package net.bald.model

import net.bald.vocab.BALD
import net.bald.Container
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF

open class ModelContainerBuilder(
    private val model: Model,
    private val uri: String,
    private val varFct: ModelVarBuilder.Factory
) {
    open fun addContainer(container: Container) {
        val rootUri = rootUri(uri)
        val containerRes = model.createResource(rootUri)
            .addProperty(RDF.type, BALD.Container)

        buildVars(container, containerRes)
    }

    private fun rootUri(uri: String): String {
        return if (uri.endsWith('/')) uri else "$uri/"
    }

    private fun buildVars(container: Container, containerRes: Resource) {
        varFct.forContainer(containerRes).apply {
            container.vars().forEach(::addVar)
        }
    }

    open class Factory(
        private val varFct: ModelVarBuilder.Factory
    ) {
        open fun forBinaryArray(model: Model, uri: String): ModelContainerBuilder {
            return ModelContainerBuilder(model, uri, varFct)
        }
    }
}




