package net.bald.netcdf

import net.bald.Dimension
import net.bald.Var
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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