package net.bald.netcdf

import net.bald.Dimension
import kotlin.test.assertEquals

/**
 * Test utility for verifying the characteristics of a [Dimension].
 * @param dim The dimension to verify.
 */
class DimensionVerifier(
    private val dim: Dimension
) {
    /**
     * Verify that the dimension has the given size.
     * @param size The expected size.
     */
    fun size(size: Int) {
        assertEquals(size, dim.size)
    }

    /**
     * Verify that the dimension corresponds to the given coordinate variable.
     * @param uri The URI of the expected coordinate variable.
     */
    fun coordinate(uri: String) {
        assertEquals(uri, dim.coordinate?.uri)
    }
}