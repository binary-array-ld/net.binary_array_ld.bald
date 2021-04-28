package bald.spec

import org.apache.jena.rdf.model.Model
import java.io.File

interface Converter {
    fun convert(
        inputLoc: File,
        uri: String? = null,
        contextLocs: List<File> = emptyList(),
        aliasLocs: List<File> = emptyList()
    ): Model
}