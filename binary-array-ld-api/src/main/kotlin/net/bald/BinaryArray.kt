package net.bald

import org.apache.jena.shared.PrefixMapping
import java.io.Closeable

/**
 * Represents the metadata of a binary array dataset.
 * See https://www.opengis.net/def/binary-array-ld/Array
 * Should be closed after use.
 */
interface BinaryArray: Closeable {
    /**
     * The prefix mapping to apply to the RDF graph.
     */
    val prefixMapping: PrefixMapping

    /**
     * The root container.
     */
    val root: Container

    /**
     * The format of the binary array.
     */
    val format: Format

    /**
     * The distribution of the binary array, if it is available. Otherwise, null.
     */
    val distribution: Distribution?
}