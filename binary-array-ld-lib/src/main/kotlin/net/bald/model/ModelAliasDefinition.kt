package net.bald.model

import net.bald.alias.AliasDefinition
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.OWL
import org.apache.jena.vocabulary.RDF

/**
 * Implementation of [AliasDefinition] that derives aliases from an RDF graph ([Model]).
 */
class ModelAliasDefinition(
    private val model: Model
): AliasDefinition {
    override fun propertyUri(identifier: String): String? {
        val resources = identifyResources(identifier)
        val props = resources.filter { resource ->
            resource.hasProperty(RDF.type, RDF.Property) || resource.hasProperty(RDF.type, OWL.ObjectProperty)
        }
        return props.firstOrNull()?.uri
    }

    override fun resourceUri(identifier: String): String? {
        val resources = identifyResources(identifier)
        return resources.firstOrNull()?.uri
    }

    private fun identifyResources(identifier: String): Sequence<Resource> {
        return model.listResourcesWithProperty(DCTerms.identifier, identifier).asSequence()
    }

    companion object {
        /**
         * Instantiate an alias definition based on a given RDF model.
         * @param model The model containing aliases.
         * @return The alias definition.
         */
        @JvmStatic
        fun create(model: Model): AliasDefinition {
            return ModelAliasDefinition(model)
        }
    }
}