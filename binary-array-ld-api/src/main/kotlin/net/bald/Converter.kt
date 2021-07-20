package net.bald

import org.apache.jena.rdf.model.Model
import java.net.URI

/**
 * A binary array to linked data converter.
 */
interface Converter {
    /**
     * Convert a binary array file to a linked data graph.
     * @param input The location of the NetCDF binary array file to convert.
     * @param uri The URI that identifies the binary array.
     * @param contexts The location of files containing JSON-LD contexts.
     * @param aliases The location of files containing alias definitions.
     * @return The linked data graph.
     */
    fun convert(
        input: URI,
        uri: String? = null,
        contexts: List<URI>? = null,
        aliases: List<URI>? = null,
    ): Model
}

