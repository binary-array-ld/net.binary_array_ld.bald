package net.bald

/**
 * A dimension that specifies the shape of a variable.
 */
interface Dimension {
    /**
     * The size of the dimension.
     */
    val size: Int

    /**
     * The coordinate variable that corresponds to the dimension, if one exists.
     * Otherwise, null.
     */
    val coordinate: Var?
}