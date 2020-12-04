package net.bald

import org.apache.jena.shared.PrefixMapping

/**
 * An entity which is described by its [Attribute]s.
 */
interface AttributeSource {
    /**
     * Obtain the list of attributes that describe this entity.
     * @param prefixMapping The prefix mapping to use to expand compact URIs.
     * @return The list of attributes.
     */
    fun attributes(prefixMapping: PrefixMapping): List<Attribute>
}