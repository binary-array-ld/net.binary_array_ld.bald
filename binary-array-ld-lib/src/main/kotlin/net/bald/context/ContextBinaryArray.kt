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
         * Decorate the given [BinaryArray] with the given [PrefixMapping] to contextualise the binary array.
         * @param ba The original binary array.
         * @param context The contextual prefix mappings.
         * @return A contextualised [BinaryArray].
         */
        @JvmStatic
        fun create(ba: BinaryArray, context: PrefixMapping): BinaryArray {
            return ContextBinaryArray(ba, context)
        }
    }
}