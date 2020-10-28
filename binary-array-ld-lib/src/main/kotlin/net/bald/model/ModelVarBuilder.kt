package net.bald.model

import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF

open class ModelVarBuilder(
    private val container: Resource
) {
    open fun addVar(v: Var) {
        val vRes = container.model.createResource("${container.uri}${v.name}", BALD.Resource)
        container.addProperty(BALD.contains, vRes)
    }

    open class Factory {
        open fun forContainer(container: Resource): ModelVarBuilder {
            return ModelVarBuilder(container)
        }
    }
}