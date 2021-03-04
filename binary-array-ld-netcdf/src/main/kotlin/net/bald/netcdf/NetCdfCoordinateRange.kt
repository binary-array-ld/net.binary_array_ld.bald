package net.bald.netcdf

import net.bald.CoordinateRange
import ucar.ma2.Array
import ucar.nc2.Variable

/**
 * NetCDF implementation of [CoordinateRange].
 */
class NetCdfCoordinateRange(
    private val v: Variable
): CoordinateRange {
    override val first: Any? get() = v.read().let(::firstElement)
    override val last: Any? get() = v.read().flip(0).let(::firstElement)

    private fun firstElement(array: Array): Any? {
        return array.takeIf(Array::hasNext)?.next()
    }
}