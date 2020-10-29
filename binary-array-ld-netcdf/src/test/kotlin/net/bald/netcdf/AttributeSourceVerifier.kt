package net.bald.netcdf

import net.bald.Attribute
import net.bald.AttributeSource
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.shared.PrefixMapping
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Test utility for verifying the [Attribute]s of an [AttributeSource].
 * @param source The attribute source to verify.
 */
class AttributeSourceVerifier(
    private val source: AttributeSource
) {
    /**
     * Begin verifying attributes on the attribute source.
     * Attributes are supplied in the order that they are declared.
     * Also verify that the number of attributes verified is equal to the total number of attributes available.
     * @param prefix The prefix mapping to apply.
     * @param verify A function to perform against the [AttributesVerifier] for the resource.
     */
    fun attributes(prefix: PrefixMapping, verify: AttributesVerifier.() -> Unit) {
        val attrs = source.attributes(prefix)
        val attrIt = attrs.iterator()
        AttributesVerifier(attrIt).verify()
        if (attrIt.hasNext()) {
            fail("Unexpected attribute on ${source}: ${attrIt.next()}")
        }
    }

    inner class AttributesVerifier(
        private val attrIt: Iterator<Attribute>
    ) {
        /**
         * Verify that the next attribute has the given properties.
         * Begin verifying values on the attribute.
         * @param uri The expected attribute URI.
         * @param name The expected attribute name.
         * @param verify A function to perform against the [ValuesVerifier] for the attribute.
         */
        fun attribute(uri: String?, name: String, verify: ValuesVerifier.() -> Unit = {}) {
            if (attrIt.hasNext()) {
                val attr = attrIt.next()
                assertEquals(uri, attr.uri, "Wrong URI on attribute $attr.")
                assertEquals(name, attr.name, "Wrong name on attribute $attr.")

                val valueIt = attr.values.iterator()
                ValuesVerifier(attr, valueIt).verify()
                if (valueIt.hasNext()) {
                    fail("Unexpected value on attribute $attr: ${valueIt.next()}")
                }
            } else {
                fail("Expected attribute with name $name on $source, but no more attributes were found.")
            }
        }
    }

    class ValuesVerifier(
        private val attr: Attribute,
        private val valueIt: Iterator<RDFNode>
    ) {
        /**
         * Verify the next attribute value.
         * @param value The expected attribute value.
         */
        fun value(value: RDFNode) {
            if (valueIt.hasNext()) {
                assertEquals(value, valueIt.next(), "Wrong value on attribute $attr.")
            } else {
                fail("Expected value $value on attribute $attr, but no more values were found.")
            }
        }
    }
}