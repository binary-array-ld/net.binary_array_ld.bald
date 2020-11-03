package net.bald

/**
 * Represents a container of resources.
 * See https://www.opengis.net/def/binary-array-ld/Container
 */
interface Container: AttributeSource {
    /**
     * The local name of the container, if it has one.
     * The root container may have no name or an empty name.
     */
    val name: String?

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