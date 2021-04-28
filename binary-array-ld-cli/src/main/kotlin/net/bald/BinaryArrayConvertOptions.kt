package net.bald

import java.io.File

/**
 * Parameters for converting a binary array into linked data.
 */
interface BinaryArrayConvertOptions {
    /**
     * The NetCDF binary array file to convert.
     */
    val inputLoc: File

    /**
     * The URI of the binary array, if it has one.
     * Otherwise, a URI will be assigned automatically.
     */
    val uri: String?

    /**
     * The files containing alias definitions.
     */
    val aliasLocs: List<File>

    /**
     * The files containing JSON-LD contexts.
     */
    val contextLocs: List<File>
}