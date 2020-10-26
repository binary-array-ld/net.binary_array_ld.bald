package net.bald.vocab

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory.createProperty
import org.apache.jena.rdf.model.ResourceFactory.createResource

/**
 * This singleton represents the Binary Array Linked Data vocabulary.
 * The variables in this file use RDF naming conventions for resources and properties.
 * See https://www.opengis.net/def/binary-array-ld/.
 */
object BALD {
    private const val prefix = "https://www.opengis.net/def/binary-array-ld/"

    /**
     * Resources
     */
    val Container: Resource = createResource("${prefix}Container")
    val Resource: Resource = createResource("${prefix}Resource")

    /**
     * Properties
     */
    val contains: Property = createProperty("${prefix}contains")
}