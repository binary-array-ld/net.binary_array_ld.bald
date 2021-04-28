package net.bald.netcdf

import net.bald.Dimension
import net.bald.Var
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

/**
 * Test utility for verifying the characteristics of a [Var].
 * @param v The variable to verify.
 */
class VarVerifier(
    private val v: Var
): AttributeSourceVerifier(v) {
    /**
     * Verify that the variable has dimensions of the given sizes, in order.
     * @param sizes The expected sizes.
     */
    fun dimensions(vararg sizes: Int) {
        val actual = v.dimensions().map(Dimension::size).toList()
        assertEquals(sizes.toList(), actual)
    }

    /**
     * Begin verifying the dimensions of the variable, in order.
     * Also verify that the number of dimensions verified is equal to the total number of dimensions available.
     * @param verify A function to perform against the [DimensionsVerifier] for the variable.
     */
    fun dimensions(verify: DimensionsVerifier.() -> Unit) {
        val dimIt = v.dimensions().iterator()
        DimensionsVerifier(dimIt).verify()
        if (dimIt.hasNext()) {
            fail("Unexpected dimension: ${dimIt.next()}")
        }
    }

    /**
     * Verify that the variable has the given coordinate range.
     * @param first The first value in the range, if it has one.
     * @param last The last value in the range, if it has one.
     */
    fun range(first: Any?, last: Any? = null) {
        assertNotNull(v.range) { range ->
            assertEquals(first, range.first)
            assertEquals(last, range.last)
        }
    }

    /**
     * Verify that the variable has references to variables of the given URIs, in order.
     * @param uris The expected URIs.
     */
    fun references(vararg uris: String) {
        val actual = v.references().map(Var::uri).toList()
        assertEquals(uris.toList(), actual)
    }
}
