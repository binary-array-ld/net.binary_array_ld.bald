package net.bald

/**
 * An entity which is described by its [Attribute]s.
 */
interface AttributeSource {
    /**
     * Obtain the list of attributes that describe this entity.
     * @return The list of attributes.
     */
    fun attributes(): Sequence<Attribute>
}