package net.bald.netcdf

import net.bald.Dimension
import kotlin.test.assertEquals

/**
 * TODO
 */
class DimensionVerifier(
    private val dim: Dimension
) {
    fun size(size: Int) {
        assertEquals(size, dim.size)
    }

    fun coordinate(uri: String) {
        assertEquals(uri, dim.coordinate?.uri)
    }
}