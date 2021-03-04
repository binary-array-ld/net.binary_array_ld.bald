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
     * TODO
     */
    fun dimensions(vararg sizes: Int) {
        val actual = v.dimensions().map(Dimension::size).toList()
        assertEquals(sizes.toList(), actual)
    }

    fun dimensions(verify: DimensionsVerifier.() -> Unit) {
        val dimIt = v.dimensions().iterator()
        DimensionsVerifier(dimIt).verify()
        if (dimIt.hasNext()) {
            fail("Unexpected dimension: ${dimIt.next()}")
        }
    }

    /**
     * TODO
     */
    fun range(first: Any?, last: Any? = null) {
        assertNotNull(v.range) { range ->
            assertEquals(first, range.first)
            assertEquals(last, range.last)
        }
    }
}
