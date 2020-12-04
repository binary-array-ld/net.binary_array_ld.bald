package net.bald.netcdf

import net.bald.Attribute
import net.bald.AttributeSource
import org.apache.jena.rdf.model.RDFNode
import kotlin.test.assertEquals
import kotlin.test.fail

class AttributesVerifier(
    private val source: AttributeSource,
    private val attrIt: Iterator<Attribute>
) {
    /**
     * Verify that the next attribute has the given URI.
     * Begin verifying values on the attribute.
     * @param uri The expected attribute URI.
     * @param verify A function to perform against the [AttributeValuesVerifier] for the attribute.
     */
    fun attribute(uri: String, verify: AttributeValuesVerifier.() -> Unit = {}) {
        if (attrIt.hasNext()) {
            val attr = attrIt.next()
            assertEquals(uri, attr.uri, "Wrong URI on attribute $attr.")

            val valueIt = attr.values.iterator()
            AttributeValuesVerifier(attr, valueIt).verify()
            if (valueIt.hasNext()) {
                fail("Unexpected value on attribute $attr: ${valueIt.next()}")
            }
        } else {
            fail("Expected attribute with URI $uri on $source, but no more attributes were found.")
        }
    }

    /**
     * Verify that the next attribute has the given URI,
     * and that the given value is its single value.
     * @see attribute
     */
    fun attribute(uri: String, value: RDFNode) {
        attribute(uri) {
            value(value)
        }
    }
}