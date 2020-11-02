package net.bald.model

import net.bald.BinaryArray
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model

class ModelBinaryArrayBuilder(
    private val model: Model,
    private val containerFct: ModelContainerBuilder.Factory
) {
    fun addBinaryArray(ba: BinaryArray) {
        val baRes = model.createResource(ba.uri, BALD.Container)
        containerFct.forParent(baRes).addContainer(ba.root)
    }

    class Factory(
        private val containerFct: ModelContainerBuilder.Factory
    ) {
        fun forModel(model: Model): ModelBinaryArrayBuilder {
            return ModelBinaryArrayBuilder(model, containerFct)
        }
    }
}