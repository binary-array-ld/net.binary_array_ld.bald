package net.bald.netcdf

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
}