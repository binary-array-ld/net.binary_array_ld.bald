package net.bald

import bald.jsonld.ResourceFileConverter
import bald.model.ModelVerifier
import bald.netcdf.CdlConverter.writeToNetCdf
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory.createDefaultModel
import org.apache.jena.rdf.model.ResourceFactory.*
import org.apache.jena.vocabulary.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
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

    /**
     * Requirements class A
     */
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
                // A-1
                statement(BALD.contains, model.createResource("$inputFileUri/")) {
                    statement(RDF.type, BALD.Container)
                    // A-2
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

    /**
     * Requirements class A
     */
    @Test
    fun run_withUri_withOutputFile_outputsToFile() {
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            // A-1
            resource("http://test.binary-array-ld.net/example") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/")) {
                    statement(RDF.type, BALD.Container)
                    // A-2
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
                    statement(BALD.isPrefixedBy, createPlainLiteral("prefix_list"))
                }
            }
        }
    }

    /**
     * Requirements class B-1
     */
    @Test
    fun run_withPrefixMappingGroup_outputsPrefixMapping() {
        run_withPrefixMapping_outputsPrefixMapping("/netcdf/prefix.cdl")
    }

    /**
     * Requirements class B-1
     */
    @Test
    fun run_withPrefixMappingVar_outputsPrefixMapping() {
        run_withPrefixMapping_outputsPrefixMapping("/netcdf/prefix-var.cdl")
    }

    /**
     * Requirements class A-2
     */
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

    /**
     * Requirements class B-4
     */
    @Test
    fun run_withExternalPrefixMapping_outputsPrefixMapping() {
        val inputFile = writeToNetCdf("/netcdf/prefix.cdl")
        val outputFile = createTempFile()
        val contextFiles = listOf(
            ResourceFileConverter.toFile("/jsonld/context.json"),
            ResourceFileConverter.toFile("/jsonld/context2.json")
        )

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--context", contextFiles.joinToString(",", transform = File::getAbsolutePath),
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            // B-8
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
                statement(BALD.isPrefixedBy, createPlainLiteral("prefix_list"))
            }
        }
    }

    /**
     * Requirements class D
     */
    @Test
    fun run_withAttributes_outputsAttributes() {
        val inputFile = writeToNetCdf("/netcdf/attributes.cdl")
        val outputFile = createTempFile()
        val contextFile = ResourceFileConverter.toFile("/jsonld/context.json")

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--context", contextFile.absolutePath,
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("dct", DCTerms.NS)
            resource("http://test.binary-array-ld.net/example/") {
                statement(DCTerms.publisher, createResource("${BALD.prefix}Organisation"))
                // D-4
                statement(createProperty("http://test.binary-array-ld.net/example/date"), createPlainLiteral("2020-10-29"))
                statement(RDF.type, BALD.Container)
                // D-2
                statement(SKOS.prefLabel, createPlainLiteral("Attributes metadata example"))
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDF.type, BALD.Resource)
                    statement(SKOS.prefLabel, createPlainLiteral("Variable 0"))
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.isPrefixedBy, createPlainLiteral("prefix_list"))
            }
        }
    }

    /**
     * Requirements class C, D
     */
    @Test
    fun run_withAliases_outputsAliasedAttributes() {
        val inputFile = writeToNetCdf("/netcdf/alias.cdl")
        val outputFile = createTempFile()
        val contextFile = ResourceFileConverter.toFile("/jsonld/context.json")
        val aliasFile = ResourceFileConverter.toFile("/turtle/alias.ttl", "ttl")

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--context", contextFile.absolutePath,
            "--alias", aliasFile.absolutePath,
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("dct", DCTerms.NS)
            resource("http://test.binary-array-ld.net/example/") {
                // D-3
                statement(DCTerms.publisher, createResource("${BALD.prefix}Organisation"))
                statement(createProperty("http://test.binary-array-ld.net/example/date"), createPlainLiteral("2020-10-29"))
                statement(RDF.type, BALD.Container)
                statement(SKOS.prefLabel, createPlainLiteral("Alias metadata example"))
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDF.type, BALD.Resource)
                    statement(RDFS.label, createPlainLiteral("var-0"))
                    statement(SKOS.prefLabel, createPlainLiteral("Variable 0"))
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.isPrefixedBy, createPlainLiteral("prefix_list"))
            }
        }
    }
}