package net.bald.model

import net.bald.BinaryArray
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.shared.PrefixMapping

class ModelBinaryArrayBuilder(
    private val model: Model,
    private val containerFct: ModelContainerBuilder.Factory
) {
    fun addBinaryArray(ba: BinaryArray) {
        addPrefixMapping(ba.prefixMapping)
        val baRes = model.createResource(ba.uri, BALD.Container)
        containerFct.forParent(baRes).addContainer(ba.root)
    }

    private fun addPrefixMapping(prefix: PrefixMapping) {
        prefix.nsPrefixMap.let(model::setNsPrefixes)
    }

    class Factory(
        private val containerFct: ModelContainerBuilder.Factory
    ) {
        fun forModel(model: Model): ModelBinaryArrayBuilder {
            return ModelBinaryArrayBuilder(model, containerFct)
        }
    }
}