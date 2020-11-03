package net.bald.model

import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF

open class ModelVarBuilder(
    private val container: Resource
) {
    open fun addVar(v: Var) {
        val varUri = varUri(v)
        val vRes = container.model.createResource(varUri, BALD.Resource)
        container.addProperty(BALD.contains, vRes)
    }

    private fun varUri(v: Var): String {
        val containerUri = container.uri
        val prefix = if (containerUri.endsWith('/')) containerUri else "$containerUri/"
        return prefix + v.name
    }

    open class Factory {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container)
        }
    }
}