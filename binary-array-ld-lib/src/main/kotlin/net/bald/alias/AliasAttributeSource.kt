package net.bald.alias

import net.bald.*
import org.apache.jena.shared.PrefixMapping

/**
 * Decorator for [AttributeSource] which supports attribute aliasing.
 */
open class AliasAttributeSource(
    private val source: AttributeSource,
    private val alias: AliasDefinition
): AttributeSource {
    override fun attributes(prefixMapping: PrefixMapping): List<Attribute> {
        return source.attributes(prefixMapping).map { attr ->
            AliasAttribute(attr, alias)
        }
    }
}