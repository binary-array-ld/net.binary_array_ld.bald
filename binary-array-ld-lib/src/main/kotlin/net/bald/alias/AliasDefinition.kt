package net.bald.alias

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

/**
 * A set of aliases for RDF properties and resources, based on their string identifiers.
 */
interface AliasDefinition {
    /**
     * Obtain the property corresponding to the given identifier, if it exists.
     * @param identifier The identifier to alias.
     * @return The corresponding alias, if it exists. Otherwise, null.
     */
    fun property(identifier: String): Property?

    /**
     * Obtain the resource corresponding to the given identifier, if it exists.
     * @param identifier The identifier to alias.
     * @return The corresponding alias, if it exists. Otherwise, null.
     */
    fun resource(identifier: String): Resource?
}