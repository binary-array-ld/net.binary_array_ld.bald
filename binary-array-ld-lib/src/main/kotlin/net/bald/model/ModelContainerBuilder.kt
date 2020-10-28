package net.bald.model

import net.bald.vocab.BALD
import net.bald.Container
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF

open class ModelContainerBuilder(
    private val ba: Resource,
    private val varFct: ModelVarBuilder.Factory
) {
    open fun addContainer(container: Container) {
        val rootUri = ba.uri + '/'
        val containerRes = ba.model.createResource(rootUri, BALD.Container)
        buildVars(container, containerRes)

        ba.addProperty(BALD.contains, containerRes)
    }

    private fun buildVars(container: Container, containerRes: Resource) {
        varFct.forContainer(containerRes).apply {
            container.vars().forEach(::addVar)
        }
    }

    open class Factory(
        private val varFct: ModelVarBuilder.Factory
    ) {
        open fun forBinaryArray(ba: Resource): ModelContainerBuilder {
            return ModelContainerBuilder(ba, varFct)
        }
    }
}




