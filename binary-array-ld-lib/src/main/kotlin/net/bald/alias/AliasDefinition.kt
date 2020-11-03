package net.bald.alias

/**
 * A set of aliases for RDF properties and resources, based on their string identifiers.
 */
interface AliasDefinition {
    /**
     * Obtain the property URI corresponding to the given identifier, if it exists.
     * @param identifier The identifier to alias.
     * @return The corresponding alias URI, if it exists. Otherwise, null.
     */
    fun propertyUri(identifier: String): String?

    /**
     * Obtain the resource URI corresponding to the given identifier, if it exists.
     * @param identifier The identifier to alias.
     * @return The corresponding alias URI, if it exists. Otherwise, null.
     */
    fun resourceUri(identifier: String): String?
}