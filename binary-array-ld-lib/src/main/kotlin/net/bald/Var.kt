package net.bald

/**
 * Represents a binary array variable.
 */
interface Var: AttributeSource {
    /**
     * The local name of the variable.
     */
    val name: String
}