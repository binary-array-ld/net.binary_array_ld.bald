package net.bald

/**
 * Represents a prefix mapping for an RDF graph.
 */
interface PrefixMapping {
    /**
     * Obtain the raw string mapping from prefix names to URIs.
     * @return The raw map.
     */
    fun toMap(): Map<String, String>

    object Empty: PrefixMapping {
        override fun toMap(): Map<String, String> {
            return emptyMap()
        }
    }
}