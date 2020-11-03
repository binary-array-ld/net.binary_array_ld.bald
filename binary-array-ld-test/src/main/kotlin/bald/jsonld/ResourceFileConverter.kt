package bald.jsonld

import java.io.File

/**
 * Test utility for converting resources to files in tests.
 * Test files are in src/main/resources.
 */
object ResourceFileConverter {
    /**
     * Loads a resource into a temporary file.
     * @param contextLoc The location of the JSON-LD context resource.
     * @param ext File extension to add to the file.
     * @return A file containing the contents of the given resource.
     */
    fun toFile(contextLoc: String, ext: String? = null): File {
        val file = createTempFile(suffix = ext?.let("."::plus))
        javaClass.getResourceAsStream(contextLoc).use { input ->
            file.outputStream().use(input::copyTo)
        }

        return file
    }
}