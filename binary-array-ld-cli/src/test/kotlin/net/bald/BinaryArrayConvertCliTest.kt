package net.bald

import bald.jsonld.ContextReader
import bald.model.ModelVerifier
import bald.netcdf.NcmlConverter.writeToNetCdf
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory.createDefaultModel
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.apache.jena.vocabulary.XSD
import org.junit.Assume
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ucar.nc2.jni.netcdf.Nc4Iosp
import java.io.File
import kotlin.test.assertEquals

/**
 * Integration test for [BinaryArrayConvertCli].
 *
 * Test resources are stored in a user-friendly format (NCML) and converted to temporary NetCDF 4 files.
 * In order to write the NetCDF 4 files, the Unidata NetCDF C library must be available.
 * Therefore, these tests are ignored if the library is unavailable and writing is impossible.
 */
class BinaryArrayConvertCliTest {
    private fun run(vararg args: String) {
        BinaryArrayConvertCli().run(*args)
    }

    @BeforeEach
    fun before() {
        Assume.assumeTrue(Nc4Iosp.isClibraryPresent())
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
        val inputFile = writeToNetCdf("/netcdf/identity.xml")
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
        val inputFile = writeToNetCdf("/netcdf/identity.xml")
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

    @Test
    fun run_withPrefixMapping_outputsPrefixMapping() {
        val inputFile = writeToNetCdf("/netcdf/prefix.xml")
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
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

    @Test
    fun run_withExternalPrefixMapping_outputsPrefixMapping() {
        val inputFile = writeToNetCdf("/netcdf/prefix.xml")
        val outputFile = createTempFile()
        val contextFiles = listOf(ContextReader.toFile("/jsonld/context.json"), ContextReader.toFile("/jsonld/context2.json"))

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--context", contextFiles.joinToString(",", transform = File::getAbsolutePath),
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("dct", DCTerms.NS)
            prefix("xsd", XSD.NS)
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