package net.bald.alias

import net.bald.vocab.BALD
import org.apache.jena.rdf.model.*
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.OWL
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS

/**
 * Implementation of [AliasDefinition] that derives aliases from an RDF graph ([Model]).
 */
class ModelAliasDefinition(
    private val model: Model
): AliasDefinition {
    override fun property(identifier: String): Property? {
        val props = identifyProperties(identifier).toList()

        return when (props.size) {
            0 -> null
            1 -> props.single().uri.let(model::createProperty)
            else -> throw IllegalStateException("Property alias $identifier is ambiguous: $props")
        }
    }

    override fun resource(identifier: String): Resource? {
        val resources = identifyResources(identifier).toList()
        return if (resources.isEmpty()) null else {
            resources.singleOrNull()
                ?: throw IllegalStateException("Resource alias $identifier is ambiguous: $resources")
        }
    }

    private fun identifyProperties(identifier: String): Sequence<Resource> {
        return identifyResources(identifier).filter { resource ->
            resource.hasProperty(RDF.type, RDF.Property) || resource.hasProperty(RDF.type, OWL.ObjectProperty)
        }
    }

    private fun identifyResources(identifier: String): Sequence<Resource> {
        return model.listResourcesWithProperty(DCTerms.identifier, identifier).asSequence()
    }

    override fun isReferenceProperty(prop: Property): Boolean {
        return prop.inModel(model).let { modelProp ->
            modelProp.hasProperty(RDFS.range, BALD.Resource)
                    || modelProp.listProperties(RDFS.range).let(::containsReferenceCls)
        }
    }

    private fun containsReferenceCls(stmts: StmtIterator, clsUris: Set<String> = emptySet()): Boolean {
        return stmts.asSequence()
            .map(Statement::getObject)
            .filter(RDFNode::isResource)
            .map(RDFNode::asResource)
            .any { cls -> isReferenceCls(cls, clsUris) }
    }

    private fun isReferenceCls(cls: Resource, clsUris: Set<String>): Boolean {
        return when {
            cls.hasProperty(RDFS.subClassOf, BALD.Resource) -> true
            clsUris.contains(cls.uri) -> false
            else -> {
                val nextClsUris = cls.uri?.let(clsUris::plus) ?: clsUris
                val parents = cls.listProperties(RDFS.subClassOf)
                containsReferenceCls(parents, nextClsUris)
            }
        }
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