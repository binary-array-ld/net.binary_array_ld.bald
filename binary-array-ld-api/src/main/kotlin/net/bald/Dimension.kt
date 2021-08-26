package net.bald

/**
 * A dimension that specifies the shape of a variable.
 */
interface Dimension {
    /**
     * The uniquely identifying name of the dimension in the binary array.
     */
    val name: String

    /**
     * The size of the dimension.
     */
    val size: Int
}