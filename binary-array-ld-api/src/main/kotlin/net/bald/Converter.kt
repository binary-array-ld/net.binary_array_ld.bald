package net.bald

import org.apache.jena.rdf.model.Model
import java.io.File

/**
 * A binary array to linked data converter.
 */
interface Converter {
    /**
     * Convert a binary array file to a linked data graph.
     * @param input The NetCDF binary array file to convert.
     * @param uri The URI of the binary array.
     * @param contexts The files containing JSON-LD contexts.
     * @param aliases The files containing alias definitions.
     * @return The linked data graph.
     */
    fun convert(
        input: File,
        uri: String? = null,
        contexts: List<File>? = null,
        aliases: List<File>? = null,
    ): Model
}

