package net.bald.netcdf

import ucar.ma2.Array

/**
 * Wrapper for a NetCDF [Array] that supports [Iterator] functionality.
 */
class ArrayIterator(
    array: Array
): Iterator<Any> {
    private val indexIt = array.indexIterator

    override fun hasNext(): Boolean {
        return indexIt.hasNext()
    }

    override fun next(): Any {
        return indexIt.next()
    }

}