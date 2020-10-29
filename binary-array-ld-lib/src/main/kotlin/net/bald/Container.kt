package net.bald

/**
 * Represents a container of resources.
 * See https://www.opengis.net/def/binary-array-ld/Container
 */
interface Container: AttributeSource {
    /**
     * Obtain the variables associates with this container.
     * @return The variables.
     */
    fun vars(): Sequence<Var>
}