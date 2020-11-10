package net.bald.context

import net.bald.BinaryArray
import net.bald.Container
import org.apache.jena.shared.PrefixMapping

/**
 * Decorator for [BinaryArray] that supports context.
 */
class ContextBinaryArray(
    private val ba: BinaryArray,
    private val context: PrefixMapping
): BinaryArray {
    override val uri: String get() = ba.uri
    override val root: Container get() = ba.root

    override val prefixMapping: PrefixMapping get() {
        return PrefixMapping.Factory.create()
            .setNsPrefixes(context)
            .setNsPrefixes(ba.prefixMapping)
    }

    override fun close() {
        ba.close()
    }

    companion object {
        /**
         * Decorate the given [BinaryArray] with the given [PrefixMapping]s to contextualise the binary array.
         * @throws IllegalArgumentException If there are conflicting definitions in the contexts.
         * @param ba The original binary array.
         * @param contexts The contextual prefix mappings.
         * @return A contextualised [BinaryArray].
         */
        @JvmStatic
        fun create(ba: BinaryArray, contexts: List<PrefixMapping>): BinaryArray {
            return if (contexts.isEmpty()) {
                ba
            } else {
                val context = contexts.reduce { acc, context ->
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
                create(ba, context)
            }
        }

        /**
         * @see create
         */
        @JvmStatic
        fun create(ba: BinaryArray, context: PrefixMapping): BinaryArray {
            return ContextBinaryArray(ba, context)
        }
    }
}