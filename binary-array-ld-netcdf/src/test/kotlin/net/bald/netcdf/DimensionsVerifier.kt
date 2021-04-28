package net.bald.netcdf

import net.bald.Dimension
import kotlin.test.fail

class DimensionsVerifier(
    private val dimIt: Iterator<Dimension>
) {
    fun dimension(verify: DimensionVerifier.() -> Unit) {
        if (dimIt.hasNext()) {
            dimIt.next().let(::DimensionVerifier).verify()
        } else {
            fail("Expected a dimension but no more were found.")
        }
    }
}