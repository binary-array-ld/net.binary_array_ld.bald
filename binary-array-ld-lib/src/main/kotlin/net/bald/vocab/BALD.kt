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
    const val prefix = "https://www.opengis.net/def/binary-array-ld/"

    /**
     * Resources
     */
    val Container: Resource = createResource("${prefix}Container")
    val Resource: Resource = createResource("${prefix}Resource")
    val Array: Resource = createResource("${prefix}Array")
    val Reference: Resource = createResource("${prefix}Reference")

    /**
     * Properties
     */
    val contains: Property = createProperty("${prefix}contains")
    val isPrefixedBy: Property = createProperty("${prefix}isPrefixedBy")
    val references: Property = createProperty("${prefix}references")
    val shape: Property = createProperty("${prefix}shape")
    val sourceRefShape: Property = createProperty("${prefix}sourceRefShape")
    val targetRefShape: Property = createProperty("${prefix}targetRefShape")
    val arrayFirstValue: Property = createProperty("${prefix}arrayFirstValue")
    val arrayLastValue: Property = createProperty("${prefix}arrayLastValue")
    val target: Property = createProperty("${prefix}target")
}