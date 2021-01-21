package net.bald.netcdf

import bald.model.ResourceVerifier
import net.bald.Attribute
import org.apache.jena.rdf.model.RDFNode
import kotlin.test.assertEquals
import kotlin.test.fail

class AttributeValuesVerifier(
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

    fun resource(verify: ResourceVerifier.() -> Unit) {
        if (valueIt.hasNext()) {
            val value = valueIt.next()
            if (value.isResource) {
                ResourceVerifier(value.asResource()).verify()
            } else {
                fail("Expected a resource value on attribute $attr, but found a literal resource instead.")
            }
        } else {
            fail("Expected a resource value on attribute $attr, but no more values were found.")
        }
    }
}