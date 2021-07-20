package net.bald.netcdf

import net.bald.Converter
import net.bald.alias.AliasDefinition
import net.bald.context.ModelContext
import net.bald.model.ModelAliasDefinition
import net.bald.model.ModelBinaryArrayConverter
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import java.net.URI

object NetCdfLdConverter: Converter {
    override fun convert(input: URI, uri: String?, contexts: List<URI>?, aliases: List<URI>?): Model {
        val fileLoc = input.toString()
        val context = context(contexts)
        val alias = alias(aliases)
        val ba = NetCdfBinaryArray.create(fileLoc, uri, context, alias)
        return ba.use(ModelBinaryArrayConverter::convert)
    }

    private fun context(contextLocs: List<URI>?): ModelContext {
        val prefixes = contextLocs?.map { contextLoc ->
            ModelFactory.createDefaultModel().read(contextLoc.toString(), "json-ld")
        } ?: emptyList()

        return ModelContext.create(prefixes)
    }

    private fun alias(aliasLocs: List<URI>?): AliasDefinition {
        return ModelFactory.createDefaultModel().apply {
            aliasLocs?.forEach { aliasLoc ->
                aliasLoc.toString().let(::read)
            }
        }.let(ModelAliasDefinition::create)
    }
}