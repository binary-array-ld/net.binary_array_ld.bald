package net.bald.netcdf

import net.bald.Converter
import net.bald.alias.AliasDefinition
import net.bald.model.ModelAliasDefinition
import net.bald.context.ModelContext
import net.bald.model.ModelBinaryArrayConverter
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import java.io.File

object NetCdfLdConverter: Converter {
    override fun convert(input: File, uri: String?, contexts: List<File>?, aliases: List<File>?): Model {
        val fileLoc = input.absolutePath
        val context = context(contexts)
        val alias = alias(aliases)
        val ba = NetCdfBinaryArray.create(fileLoc, uri, context, alias)
        return ba.use(ModelBinaryArrayConverter::convert)
    }

    private fun context(contextLocs: List<File>?): ModelContext {
        val prefixes = contextLocs?.map { contextLoc ->
            ModelFactory.createDefaultModel().read(contextLoc.absolutePath, "json-ld")
        } ?: emptyList()

        return ModelContext.create(prefixes)
    }

    private fun alias(aliasLocs: List<File>?): AliasDefinition {
        return ModelFactory.createDefaultModel().apply {
            aliasLocs?.forEach { aliasLoc ->
                aliasLoc.absolutePath.let(::read)
            }
        }.let(ModelAliasDefinition::create)
    }
}