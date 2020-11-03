package net.bald.alias

import net.bald.BinaryArray
import net.bald.Container
import org.apache.jena.shared.PrefixMapping

/**
 * Decorator for [BinaryArray] which supports attribute aliasing.
 */
class AliasBinaryArray(
    private val ba: BinaryArray,
    private val alias: AliasDefinition
): BinaryArray {
    override val uri: String get() = ba.uri
    override val prefixMapping: PrefixMapping get() = ba.prefixMapping
    override val root: Container get() = AliasContainer(ba.root, alias)

    override fun close() {
        ba.close()
    }

    companion object {
        /**
         * Decorate the given [BinaryArray] with the given [AliasDefinition] to support attribute aliasing.
         * @param ba The original binary array.
         * @param alias The alias definition to apply.
         * @return An aliased [BinaryArray].
         */
        @JvmStatic
        fun create(ba: BinaryArray, alias: AliasDefinition): BinaryArray {
            return AliasBinaryArray(ba, alias)
        }
    }
}