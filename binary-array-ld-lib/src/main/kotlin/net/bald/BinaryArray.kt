package net.bald

import org.apache.jena.shared.PrefixMapping

/**
 * Represents the metadata of a binary array dataset.
 * See https://www.opengis.net/def/binary-array-ld/Array
 */
interface BinaryArray {
    /**
     * The URI which identifies the dataset.
     */
    val uri: String

    /**
     * The prefix mapping to apply to the RDF graph.
     */
    val prefixMapping: PrefixMapping

    /**
     * The root container.
     */
    val root: Container
}