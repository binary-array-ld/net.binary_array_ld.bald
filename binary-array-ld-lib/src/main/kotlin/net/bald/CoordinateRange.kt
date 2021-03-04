package net.bald

/**
 * The range of values that is spanned by a coordinate variable.
 */
interface CoordinateRange {
    /**
     * The first value in the range, if it has one.
     * Otherwise, null.
     */
    val first: Any?

    /**
     * The last value in the range, if it has one.
     * Otherwise, null.
     */
    val last: Any?
}