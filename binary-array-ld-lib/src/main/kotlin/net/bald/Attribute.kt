package net.bald

import org.apache.jena.rdf.model.RDFNode

/**
 * A description of a property of an [AttributeSource] entity.
 */
interface Attribute {
    /**
     * The URI which identifies the attribute, if it has one. Otherwise, null.
     */
    val uri: String

    /**
     * The values of the attribute, expressed as RDF resource or literal nodes.
     */
    val values: List<RDFNode>
}