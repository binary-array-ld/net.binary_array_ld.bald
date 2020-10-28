package bald.jsonld

import java.io.File

/**
 * Test utility for handling JSON-LD context files in tests.
 * Test files are in src/main/resources/jsonld.
 */
object ContextReader {
    /**
     * Loads a JSON-LD context resource into a temporary file.
     * @param contextLoc The location of the JSON-LD context resource.
     * @return A file containing the contents of the given resource.
     */
    fun toFile(contextLoc: String): File {
        val file = createTempFile()
        javaClass.getResourceAsStream(contextLoc).use { input ->
            file.outputStream().use(input::copyTo)
        }

        return file
    }
}