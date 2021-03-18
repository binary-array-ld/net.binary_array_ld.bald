package net.bald

/**
 * Represents a container of resources.
 * See https://www.opengis.net/def/binary-array-ld/Container
 */
interface Container: AttributeSource {
    /**
     * The URI of the container.
     */
    val uri: String

    /**
     * Obtain the variables associates with this container.
     * @return The variables.
     */
    fun vars(): Sequence<Var>

    /**
     * Obtain the sub-containers of this container.
     * @return The containers.
     */
    fun subContainers(): Sequence<Container>
}