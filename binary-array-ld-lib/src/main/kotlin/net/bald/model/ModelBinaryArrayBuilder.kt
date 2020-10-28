package net.bald.model

import net.bald.BinaryArray
import net.bald.PrefixMapping
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model

class ModelBinaryArrayBuilder(
    private val model: Model,
    private val containerFct: ModelContainerBuilder.Factory
) {
    fun addBinaryArray(ba: BinaryArray) {
        addPrefixMapping(ba.prefixMapping)
        val baRes = model.createResource(ba.uri, BALD.Container)
        containerFct.forBinaryArray(baRes).addContainer(ba.root)
    }

    private fun addPrefixMapping(prefix: PrefixMapping) {
        prefix.toMap().let(model::setNsPrefixes)
    }

    class Factory(
        private val containerFct: ModelContainerBuilder.Factory
    ) {
        fun forModel(model: Model): ModelBinaryArrayBuilder {
            return ModelBinaryArrayBuilder(model, containerFct)
        }
    }
}