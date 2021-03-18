package net.bald.netcdf

import net.bald.Attribute
import net.bald.AttributeSource
import kotlin.test.fail

/**
 * Test utility for verifying the [Attribute]s of an [AttributeSource].
 * @param source The attribute source to verify.
 */
open class AttributeSourceVerifier(
    private val source: AttributeSource
) {
    /**
     * Begin verifying attributes on the attribute source.
     * Attributes are supplied in the order that they are declared.
     * Also verify that the number of attributes verified is equal to the total number of attributes available.
     * @param verify A function to perform against the [AttributesVerifier] for the source.
     */
    fun attributes(verify: AttributesVerifier.() -> Unit) {
        val attrs = source.attributes()
        val attrIt = attrs.iterator()
        AttributesVerifier(source, attrIt).verify()
        if (attrIt.hasNext()) {
            fail("Unexpected attribute on ${source}: ${attrIt.next()}")
        }
    }
}