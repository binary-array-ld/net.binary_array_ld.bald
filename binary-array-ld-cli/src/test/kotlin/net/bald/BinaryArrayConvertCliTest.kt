package net.bald

import bald.model.ModelVerifier
import bald.netcdf.NcmlConverter.convertToNetCdf
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory.createDefaultModel
import org.apache.jena.vocabulary.RDF
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * Integration test for [BinaryArrayConvertCli].
 */
class BinaryArrayConvertCliTest {
    private fun run(vararg args: String) {
        BinaryArrayConvertCli().run(*args)
    }

    @Test
    fun run_withoutInputFile_fails() {
        val iae = assertThrows<IllegalArgumentException> {
            run()
        }
        assertEquals("First argument is required: NetCDF file to convert.", iae.message)
    }

    @Test
    fun run_withHelp_doesNotValidate() {
        run("-h")
    }

    @Test
    fun run_withoutUri_outputsToFileWithInputFileUri() {
        val inputFile = convertToNetCdf("/netcdf/identity.xml")
        val inputFileUri = inputFile.toPath().toUri()
        val outputFile = createTempFile()
        run(inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("$inputFileUri/") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("$inputFileUri/var0")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("$inputFileUri/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
            }
        }
    }

    @Test
    fun run_withUri_withOutputFile_outputsToFile() {
        val inputFile = convertToNetCdf("/netcdf/identity.xml")
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("http://test.binary-array-ld.net/example/") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
            }
        }
    }
}