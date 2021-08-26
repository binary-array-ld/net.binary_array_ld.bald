package net.bald

import org.apache.jena.rdf.model.Resource

/**
 * A binary array format.
 */
interface Format {
    /**
     * The resource which represents the format.
     */
    val identifier: Resource
}