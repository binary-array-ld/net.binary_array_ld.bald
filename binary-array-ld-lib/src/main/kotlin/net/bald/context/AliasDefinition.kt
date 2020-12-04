package net.bald.context

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

    /**
     * Determine whether the given property represents a reference to another variable.
     * The property may be obtained from [property] or from another model (or no model).
     * @param prop The property to check.
     * @return True if the property is a reference to another variable. Otherwise, false.
     */
    fun isReferenceProperty(prop: Property): Boolean

    object Empty: AliasDefinition {
        override fun property(identifier: String): Property? {
            return null
        }

        override fun resource(identifier: String): Resource? {
            return null
        }

        override fun isReferenceProperty(prop: Property): Boolean {
            return false
        }
    }
}