package net.bald.netcdf

import net.bald.Attribute
import net.bald.AttributeSource
import kotlin.test.assertEquals
import kotlin.test.fail

class AttributesVerifier(
    private val source: AttributeSource,
    private val attrIt: Iterator<Attribute>
) {
    /**
     * Verify that the next attribute has the given properties.
     * Begin verifying values on the attribute.
     * @param uri The expected attribute URI.
     * @param name The expected attribute name.
     * @param verify A function to perform against the [AttributeValuesVerifier] for the attribute.
     */
    fun attribute(uri: String?, name: String, verify: AttributeValuesVerifier.() -> Unit = {}) {
        if (attrIt.hasNext()) {
            val attr = attrIt.next()
            assertEquals(uri, attr.uri, "Wrong URI on attribute $attr.")
            assertEquals(name, attr.name, "Wrong name on attribute $attr.")

            val valueIt = attr.values.iterator()
            AttributeValuesVerifier(attr, valueIt).verify()
            if (valueIt.hasNext()) {
                fail("Unexpected value on attribute $attr: ${valueIt.next()}")
            }
        } else {
            fail("Expected attribute with name $name on $source, but no more attributes were found.")
        }
    }
}