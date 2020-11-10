package net.bald.model

import net.bald.BinaryArray
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.shared.PrefixMapping
import java.net.URI

class ModelBinaryArrayBuilder(
    private val model: Model,
    private val containerFct: ModelContainerBuilder.Factory
) {
    fun addBinaryArray(ba: BinaryArray) {
        addPrefixMapping(ba.prefixMapping)
        val baRes = model.createResource(ba.uri, BALD.Container)
        containerFct.forParent(baRes).addContainer(ba.root)
    }

    private fun addPrefixMapping(prefixMapping: PrefixMapping) {
        prefixMapping.nsPrefixMap.onEach { (prefix, uri) ->
            validatePrefixMapping(prefix, uri)
        }.let(model::setNsPrefixes)
    }

    private fun validatePrefixMapping(prefix: String, uri: String) {
        try {
            if (!Prefix.pattern.matches(prefix)) {
                throw IllegalArgumentException("Prefix must match pattern ${Prefix.pattern}.")
            } else if (!uri.endsWith('/') && !uri.endsWith('#')) {
                throw IllegalArgumentException("URI must end with / or #.")
            } else {
                val scheme = URI.create(uri).scheme
                if (scheme != "http" && scheme != "https") {
                    throw IllegalArgumentException("URI must have HTTP or HTTPS scheme.")
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Unable to add prefix mapping $prefix to model: ${e.message}")
        }
    }

    private object Prefix {
        val pattern = Regex("[A-Za-z_]+")
    }

    class Factory(
        private val containerFct: ModelContainerBuilder.Factory
    ) {
        fun forModel(model: Model): ModelBinaryArrayBuilder {
            return ModelBinaryArrayBuilder(model, containerFct)
        }
    }
}