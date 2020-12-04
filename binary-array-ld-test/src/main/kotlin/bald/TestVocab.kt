package bald

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.ResourceFactory.createProperty

/**
 * Vocabulary containing terms that are created for test purposes.
 */
object TestVocab {
    const val prefix = "http://test.binary-array-ld.net/vocab/"

    /**
     * Properties
     */
    val rootVar: Property = createProperty("${prefix}root_var")
    val parentVar: Property = createProperty("${prefix}parent_var")
    val siblingVar: Property = createProperty("${prefix}sibling_var")
}