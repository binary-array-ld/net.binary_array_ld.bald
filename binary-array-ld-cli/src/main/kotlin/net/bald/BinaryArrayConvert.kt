package net.bald

import net.bald.alias.AliasDefinition
import net.bald.alias.ModelAliasDefinition
import net.bald.context.ModelContext
import net.bald.model.ModelBinaryArrayConverter
import net.bald.netcdf.NetCdfBinaryArray
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import java.io.File

object BinaryArrayConvert {
    fun convert(opts: BinaryArrayConvertOptions): Model {
        val fileLoc = opts.inputLoc.absolutePath
        val context = context(opts.contextLocs)
        val alias = alias(opts.aliasLocs)
        val ba = NetCdfBinaryArray.create(fileLoc, opts.uri, context, alias)
        return ba.use(ModelBinaryArrayConverter::convert)
    }

    private fun context(contextLocs: List<File>): ModelContext {
        val prefixes = contextLocs.map { contextLoc ->
            ModelFactory.createDefaultModel().read(contextLoc.absolutePath, "json-ld")
        }

        return ModelContext.create(prefixes)
    }

    private fun alias(aliasLocs: List<File>): AliasDefinition {
        return ModelFactory.createDefaultModel().apply {
            aliasLocs.forEach { aliasLoc ->
                aliasLoc.absolutePath.let(::read)
            }
        }.let(ModelAliasDefinition::create)
    }
}