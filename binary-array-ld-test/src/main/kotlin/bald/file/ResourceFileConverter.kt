package bald.file

import java.io.File

/**
 * Test utility for converting classpath resources to files in tests.
 * Resources are in src/main/resources.
 */
object ResourceFileConverter {
    /**
     * Loads a resource into a temporary file.
     * @param loc The location of the classpath resource.
     * @param ext File extension to add to the file.
     * @return A file containing the contents of the given resource.
     */
    fun toFile(loc: String, ext: String? = null): File {
        val file = createTempFile(suffix = ext?.let("."::plus))
        javaClass.getResourceAsStream(loc).use { input ->
            file.outputStream().use(input::copyTo)
        }

        return file
    }
}