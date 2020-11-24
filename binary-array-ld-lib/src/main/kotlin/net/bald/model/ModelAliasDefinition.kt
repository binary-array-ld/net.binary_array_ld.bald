package net.bald.model

import net.bald.alias.AliasDefinition
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdf.model.ResourceFactory.createProperty
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.OWL
import org.apache.jena.vocabulary.RDF

/**
 * Implementation of [AliasDefinition] that derives aliases from an RDF graph ([Model]).
 */
class ModelAliasDefinition(
    private val model: Model
): AliasDefinition {
    override fun property(identifier: String): Property? {
        val resources = identifyResources(identifier)
        val props = resources.filter { resource ->
            resource.hasProperty(RDF.type, RDF.Property) || resource.hasProperty(RDF.type, OWL.ObjectProperty)
        }.toList()

        return if (props.isEmpty()) null else {
            props.singleOrNull()?.uri?.let(::createProperty)
                ?: throw IllegalStateException("Property alias $identifier is ambiguous: $props")
        }
    }

    override fun resource(identifier: String): Resource? {
        val resources = identifyResources(identifier).toList()
        return if (resources.isEmpty()) null else {
            resources.singleOrNull()
                ?: throw IllegalStateException("Resource alias $identifier is ambiguous: $resources")
        }
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