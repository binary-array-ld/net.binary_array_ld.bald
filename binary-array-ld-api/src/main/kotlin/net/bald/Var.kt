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
     * The coordinate range spanned by the variable, if it has one.
     */
    val range: CoordinateRange?

    /**
     * The dimensions that specify the shape of the variable.
     */
    fun dimensions(): Sequence<Dimension>

    /**
     * The other variables which the variable references.
     */
    fun references(): Sequence<Var>
}