package net.bald.context

import org.apache.jena.shared.PrefixMapping

/**
 * The external context in which a binary array can be resolved.
 * This includes any available namespace prefix mappings.
 */
interface ModelContext {
    /**
     * Prefix mappings that are available in this context.
     */
    val prefixMapping: PrefixMapping

    /**
     * A substitute [ModelContext] implementation that represents a null or empty context.
     */
    object Empty: ModelContext {
        override val prefixMapping: PrefixMapping get() = PrefixMapping.Factory.create()
    }

    /**
     * A basic [ModelContext] implementation that simply composes the prefix mapping and alias elements.
     */
    class Base(
        override val prefixMapping: PrefixMapping
    ): ModelContext

    companion object {
        /**
         * Instantiate a wrapper for the external elements which contextualise a binary array conversion.
         * @throws IllegalArgumentException If there are conflicting definitions in the prefix mappings.
         * @param prefixes The contextual prefix mappings.
         * @return The [ModelContext].
         */
        @JvmStatic
        fun create(prefixes: List<PrefixMapping>): ModelContext {
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
            return create(prefix)
        }

        /**
         * @see create
         */
        @JvmStatic
        fun create(prefix: PrefixMapping): ModelContext {
            return Base(prefix)
        }
    }
}