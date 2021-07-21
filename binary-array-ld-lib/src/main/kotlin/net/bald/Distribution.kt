package net.bald

import org.apache.jena.rdf.model.Resource

/**
 * A distribution of a binary array file.
 */
interface Distribution {
    /**
     * The media type of the binary array file.
     */
    val mediaType: String

    /**
     * The URL from which this distribution of the file can be downloaded, if it has one.
     * Otherwise, null.
     */
    val downloadUrl: Resource?
}