package net.bald

import bald.model.ModelVerifier
import bald.netcdf.CdlConverter.writeToNetCdf
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory.createDefaultModel
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.junit.Assume
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ucar.nc2.jni.netcdf.Nc4Iosp
import kotlin.test.assertEquals

/**
 * Integration test for [BinaryArrayConvertCli].
 *
 * Test resources are stored in CDL format and converted to temporary NetCDF 4 files.
 * In order to write the NetCDF 4 files, the ncgen command line utility must be available.
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
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
        val inputFileUri = inputFile.toPath().toUri().toString()
        val outputFile = createTempFile()
        run(inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource(inputFileUri) {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("$inputFileUri/")) {
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
    }

    @Test
    fun run_withUri_withOutputFile_outputsToFile() {
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
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

    private fun run_withPrefixMapping_outputsPrefixMapping(cdlLoc: String) {
        val inputFile = writeToNetCdf(cdlLoc)
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            resource("http://test.binary-array-ld.net/example") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/")) {
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

    @Test
    fun run_withPrefixMappingGroup_outputsPrefixMapping() {
        run_withPrefixMapping_outputsPrefixMapping("/netcdf/prefix.cdl")
    }

    @Test
    fun run_withPrefixMappingVar_outputsPrefixMapping() {
        run_withPrefixMapping_outputsPrefixMapping("/netcdf/prefix-var.cdl")
    }

    @Test
    fun run_withSubgroups_outputsWithSubgroups() {
        val inputFile = writeToNetCdf("/netcdf/identity-subgroups.cdl")
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("http://test.binary-array-ld.net/example") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/")) {
                    statement(RDF.type, BALD.Container)
                    statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group0")) {
                        statement(RDF.type, BALD.Container)
                        statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group0/var2")) {
                            statement(RDF.type, BALD.Resource)
                        }
                        statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group0/var3")) {
                            statement(RDF.type, BALD.Resource)
                        }
                    }
                    statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group1")) {
                        statement(RDF.type, BALD.Container)
                        statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group1/var4")) {
                            statement(RDF.type, BALD.Resource)
                        }
                        statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/group1/var5")) {
                            statement(RDF.type, BALD.Resource)
                        }
                    }
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
}