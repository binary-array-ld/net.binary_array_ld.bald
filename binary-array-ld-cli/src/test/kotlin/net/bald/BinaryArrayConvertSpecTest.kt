package net.bald

import bald.spec.BaseSpecTest
import bald.spec.Converter
import org.apache.jena.rdf.model.Model
import java.io.File

class BinaryArrayConvertSpecTest: BaseSpecTest(BinaryArrayTestConverter)

object BinaryArrayTestConverter: Converter {
    override fun convert(
        inputLoc: File,
        uri: String?,
        contextLocs: List<File>,
        aliasLocs: List<File>
    ): Model {
        val opts = object: BinaryArrayConvertOptions {
            override val inputLoc: File get() = inputLoc
            override val uri: String? get() = uri
            override val aliasLocs: List<File> get() = aliasLocs
            override val contextLocs: List<File> get() = contextLocs
        }

        return BinaryArrayConvert.convert(opts)
    }
}