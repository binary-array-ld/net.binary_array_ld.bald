package net.bald.context

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.shared.PrefixMapping

/**
 * The external context in which a binary array can be resolved.
 * This includes resource and property aliases as defined by [AliasDefinition]
 * and the available namespace prefix mappings.
 * @see AliasDefinition
 */
interface ModelContext: AliasDefinition {
    /**
     * Prefix mappings that are available in this context.
     */
    val prefixMapping: PrefixMapping

    /**
     * A substitute [ModelContext] implementation that represents a null or empty context.
     */
    object Empty: ModelContext {
        override val prefixMapping: PrefixMapping get() = PrefixMapping.Factory.create()
        override fun property(identifier: String): Property? = null
        override fun resource(identifier: String): Resource? = null
        override fun isReferenceProperty(prop: Property): Boolean = false
    }

    /**
     * A basic [ModelContext] implementation that simply composes the prefix mapping and alias elements.
     */
    class Base(
        override val prefixMapping: PrefixMapping,
        private val alias: AliasDefinition
    ): ModelContext {
        override fun property(identifier: String): Property? {
            return alias.property(identifier)
        }

        override fun resource(identifier: String): Resource? {
            return alias.resource(identifier)
        }

        override fun isReferenceProperty(prop: Property): Boolean {
            return alias.isReferenceProperty(prop)
        }
    }

    companion object {
        /**
         * Instantiate a wrapper for the external elements which contextualise a binary array conversion.
         * @throws IllegalArgumentException If there are conflicting definitions in the prefix mappings.
         * @param prefixes The contextual prefix mappings.
         * @param alias The contextual alias definition.
         * @return The [ModelContext].
         */
        @JvmStatic
        fun create(prefixes: List<PrefixMapping>, alias: AliasDefinition? = null): ModelContext {
            val prefix = if (prefixes.isEmpty()) {
                PrefixMapping.Factory.create()
            } else {
                prefixes.reduce { acc, context ->
                    val accKeys = acc.nsPrefixMap.keys
                    val contextKeys = context.nsPrefixMap.keys
                    val conflicts = accKeys.intersect(contextKeys).filterNot { prefix ->
                        acc.getNsPrefixURI(prefix) == context.getNsPrefixURI(prefix)
                    }

                    if (conflicts.isEmpty()) {
                        acc.setNsPrefixes(context)
                    } else {
                        throw IllegalArgumentException("The namespace prefixes $conflicts have conflicting definitions in contexts.")
                    }
                }
            }
            return create(prefix, alias)
        }

        /**
         * @see create
         */
        @JvmStatic
        fun create(prefix: PrefixMapping, alias: AliasDefinition? = null): ModelContext {
            return Base(prefix, alias ?: AliasDefinition.Empty)
        }
    }
}