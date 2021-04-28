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
    val references: Property = createProperty("${prefix}references")
    val rootVar: Property = createProperty("${prefix}root_var")
    val parentVar: Property = createProperty("${prefix}parent_var")
    val siblingVar: Property = createProperty("${prefix}sibling_var")
    val orderedVar: Property = createProperty("${prefix}ordered_var")
    val unorderedVar: Property = createProperty("${prefix}unordered_var")
}