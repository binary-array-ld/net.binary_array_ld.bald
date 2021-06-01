package net.bald.netcdf

import net.bald.Dimension
import kotlin.test.assertEquals
import kotlin.test.fail

class DimensionsVerifier(
    private val dimIt: Iterator<Dimension>
) {
    fun dimension(name: String, verify: DimensionVerifier.() -> Unit) {
        if (dimIt.hasNext()) {
            val dim = dimIt.next()
            assertEquals(name, dim.name)
            DimensionVerifier(dim).verify()
        } else {
            fail("Expected a dimension but no more were found.")
        }
    }
}