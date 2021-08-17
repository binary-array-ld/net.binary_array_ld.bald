package net.bald.model

import net.bald.BinaryArray
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCAT
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import java.net.URI

class ModelBinaryArrayBuilder(
    private val model: Model,
    private val containerFct: ModelContainerBuilder.Factory
) {
    fun addBinaryArray(ba: BinaryArray) {
        val root = ba.root
        val rootUri = root.uri
        val res = model.createResource(rootUri)

        addPrefixMapping(ba.prefixMapping, rootUri)
        addFormat(ba, res)
        addDistribution(ba, res)
        containerFct.forRoot(model).addContainer(root)
    }

    private fun addFormat(ba: BinaryArray, root: Resource) {
        val format = model.createResource()
            .addProperty(RDF.type, DCTerms.MediaType)
            .addProperty(DCTerms.identifier, ba.format.identifier)
        root.addProperty(DCTerms.format, format)
    }

    private fun addDistribution(ba: BinaryArray, root: Resource) {
        ba.distribution?.let { dist ->
            val mediaType = model.createResource()
                .addProperty(RDF.type, DCTerms.MediaType)
                .addProperty(DCTerms.identifier, dist.mediaType)
            val distribution = model.createResource()
                .addProperty(RDF.type, DCAT.Distribution)
                .addProperty(DCAT.mediaType, mediaType)
                .apply {
                    dist.downloadUrl?.let { downloadUrl ->
                        addProperty(DCAT.downloadURL, downloadUrl)
                    }
                }
            root.addProperty(DCAT.distribution, distribution)
        }
    }

    private fun addPrefixMapping(prefixMapping: PrefixMapping, rootUri: String) {
        prefixMapping.nsPrefixMap.onEach { (prefix, uri) ->
            validatePrefixMapping(prefix, uri)
        }.let(model::setNsPrefixes)

        if (model.getNsPrefixURI("this") == null) {
            model.setNsPrefix("this", rootUri)
        }
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