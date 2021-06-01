package bald.json

import bald.file.ResourceFileConverter
import bald.model.ResourceModelConverter
import bald.netcdf.CdlConverter
import bald.spec.SpecRequirement
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import net.bald.Converter
import org.apache.jena.rdf.model.Model
import java.io.File

class JsonSpecRequirement(
    override val name: String,
    override val comment: String? = null,
    private val root: String? = null,
    private val input: Input,
    private val output: Output,
): SpecRequirement {
    override fun result(converter: Converter): Model {
        return input.convert(this, converter)
    }

    override fun expectation(): Model {
        return output.graph(this)
    }

    fun locate(path: String): String {
        return if (root == null) path else "$root/$path"
    }

    class Input(
        private val file: String,
        private val uri: String? = null,
        @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
        private val context: List<String> = emptyList(),
        @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
        private val alias: List<String> = emptyList()
    ) {
        fun convert(parent: JsonSpecRequirement, converter: Converter): Model {
            val inputLoc: File = parent.locate(file).let(CdlConverter::writeToNetCdf)
            val contextLocs: List<File> = files(context)
            val aliasLocs: List<File> = files(alias)
            return converter.convert(inputLoc, uri, contextLocs, aliasLocs)
        }

        private fun files(resourceLocs: List<String>): List<File> {
            return resourceLocs.map { loc ->
                val ext = loc.substringAfterLast(".", "tmp")
                ResourceFileConverter.toFile(loc, ext)
            }
        }
    }

    class Output @JsonCreator constructor(
        private val file: String
    ) {
        fun graph(parent: JsonSpecRequirement): Model {
            return parent.locate(file).let(ResourceModelConverter::toModel)
        }
    }

    override fun toString(): String {
        return name
    }
}