package net.bald

/**
 * Represents a binary array variable.
 */
interface Var: AttributeSource {
    /**
     * The URI of the variable.
     */
    val uri: String

    /**
     * TODO
     */
    val range: CoordinateRange?

    /**
     * TODO
     */
    fun dimensions(): Sequence<Dimension>
}