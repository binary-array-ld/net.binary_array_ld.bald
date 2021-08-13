package net.bald

import bald.TestVocab
import bald.jsonld.ResourceFileConverter
import bald.model.ModelVerifier
import bald.model.StatementsVerifier
import bald.netcdf.CdlConverter.writeToNetCdf
import com.fasterxml.jackson.databind.ObjectMapper
import net.bald.vocab.BALD
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.ModelFactory.createDefaultModel
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory.*
import org.apache.jena.riot.RiotException
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
            prefix("this", "$inputFileUri/")
            // A-1
            resource("$inputFileUri/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
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
            prefix("this", "http://test.binary-array-ld.net/example/")
            // A-1
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
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

    @Test
    fun run_withDownloadUrl_outputsDownloadUrl() {
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
        val outputFile = createTempFile()
        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--download", "http://test.binary-array-ld.net/download/example.nc",
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution("http://test.binary-array-ld.net/download/example.nc")
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
    fun run_withOutputFormat_outputsToFile() {
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
        val outputFile = createTempFile()
        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--output", "json-ld",
            inputFile.absolutePath,
            outputFile.absolutePath
        )
        val result = ObjectMapper().readTree(outputFile)
        // Unable to test JSON-LD directly due to unpredictable ordering of blank nodes
        assertEquals(setOf("@context", "@graph"), result.fieldNames().asSequence().toSet())
    }

    @Test
    fun run_withInvalidFormat_throwsException() {
        val inputFile = writeToNetCdf("/netcdf/identity.cdl")
        val outputFile = createTempFile()
        val re = assertThrows<RiotException> {
            run(
                "--uri", "http://test.binary-array-ld.net/example",
                "--output", "foo",
                inputFile.absolutePath,
                outputFile.absolutePath
            )
        }
        assertEquals("No graph writer for 'foo'", re.message)
    }

    private fun run_withPrefixMapping_outputsPrefixMapping(cdlLoc: String) {
        val inputFile = writeToNetCdf(cdlLoc)
        val outputFile = createTempFile()
        run("--uri", "http://test.binary-array-ld.net/example", inputFile.absolutePath, outputFile.absolutePath)

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("this", "http://test.binary-array-ld.net/example/")
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
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
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
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
                format()
                statement(RDF.type, BALD.Container)
                distribution()
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
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
                format()
                statement(DCTerms.publisher, createResource("${BALD.prefix}Organisation"))
                // D-4
                statement(createProperty("http://test.binary-array-ld.net/example/date"), createPlainLiteral("2020-10-29"))
                statement(RDF.type, BALD.Container)
                // D-2
                statement(SKOS.prefLabel, createPlainLiteral("Attributes metadata example"))
                distribution()
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDF.type, BALD.Resource)
                    statement(SKOS.prefLabel, createPlainLiteral("Variable 0"))
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
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
                format()
                // D-3
                statement(DCTerms.publisher, createResource("${BALD.prefix}Organisation"))
                statement(createProperty("http://test.binary-array-ld.net/example/date"), createPlainLiteral("2020-10-29"))
                statement(RDF.type, BALD.Container)
                statement(SKOS.prefLabel, createPlainLiteral("Alias metadata example"))
                distribution()
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDF.type, BALD.Resource)
                    statement(RDFS.label, createPlainLiteral("var-0"))
                    statement(SKOS.prefLabel, createPlainLiteral("Variable 0"))
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(RDF.type, BALD.Resource)
                }
            }
        }
    }

    /**
     * Requirements class E-1, E-2
     */
    @Test
    fun run_withVariableValueAttributes_outputsVariableValues() {
        val inputFile = writeToNetCdf("/netcdf/var-ref.cdl")
        val outputFile = createTempFile()
        val contextFile = ResourceFileConverter.toFile("/jsonld/context.json")
        val aliasFile = ResourceFileConverter.toFile("/turtle/var-alias.ttl", "ttl")

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
                format()
                statement(TestVocab.orderedVar) {
                    list(
                        createResource("http://test.binary-array-ld.net/example/var0"),
                        createResource("http://test.binary-array-ld.net/example/foo/bar/var2"),
                        createResource("http://test.binary-array-ld.net/example/baz/var3")
                    )
                }
                statement(TestVocab.rootVar, createResource("http://test.binary-array-ld.net/example/var0"))
                statement(TestVocab.unorderedVar, createResource("http://test.binary-array-ld.net/example/foo/bar/var2"))
                statement(TestVocab.unorderedVar, createResource("http://test.binary-array-ld.net/example/foo/var1"))
                statement(TestVocab.unorderedVar, createResource("http://test.binary-array-ld.net/example/var0"))
                statement(RDF.type, BALD.Container)
                statement(SKOS.prefLabel, createPlainLiteral("Variable reference metadata example"))
                distribution()
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/baz")) {
                    statement(RDF.type, BALD.Container)
                    statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/baz/var3")) {
                        statement(RDF.type, BALD.Resource)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo")) {
                    statement(TestVocab.rootVar, createResource("http://test.binary-array-ld.net/example/var0"))
                    statement(TestVocab.siblingVar, createResource("http://test.binary-array-ld.net/example/baz/var3"))
                    statement(RDF.type, BALD.Container)
                    statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo/bar")) {
                        statement(RDF.type, BALD.Container)
                        statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo/bar/var2")) {
                            statement(TestVocab.parentVar, createResource("http://test.binary-array-ld.net/example/foo/var1"))
                            statement(RDF.type, BALD.Resource)
                            statement(SKOS.prefLabel, createPlainLiteral("var2"))
                        }
                    }
                    statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/foo/var1")) {
                        statement(TestVocab.references, createPlainLiteral("var9"))
                        statement(TestVocab.siblingVar, createResource("http://test.binary-array-ld.net/example/foo/bar/var2"))
                        statement(RDF.type, BALD.Resource)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/var0")) {
                    statement(RDF.type, BALD.Resource)
                }
            }
        }
    }

    @Test
    fun run_withCoordinateVars_outputsCoordinateRanges() {
        val aliasFile = ResourceFileConverter.toFile("/turtle/alias.ttl", "ttl")
        val inputFile = writeToNetCdf("/netcdf/coordinate-var.cdl")
        val outputFile = createTempFile()

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--alias", aliasFile.absolutePath,
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/elev"), sortAnon = ::sortRefs) {
                    statement(RDF.type, BALD.Array)
                    statement(RDFS.label, createPlainLiteral("height"))
                    statement(BALD.references) {
                        statement(RDF.type, BALD.Reference)
                        statement(BALD.sourceRefShape) {
                            list(15, 10)
                        }
                        statement(BALD.target, createResource("http://test.binary-array-ld.net/example/lat"))
                        statement(BALD.targetRefShape) {
                            list(15, 1)
                        }
                    }
                    statement(BALD.references) {
                        statement(RDF.type, BALD.Reference)
                        statement(BALD.sourceRefShape) {
                            list(15, 10)
                        }
                        statement(BALD.target, createResource("http://test.binary-array-ld.net/example/lon"))
                        statement(BALD.targetRefShape) {
                            list(1, 10)
                        }
                    }
                    statement(BALD.shape) {
                        list(15, 10)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/lat")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDFS.label, createPlainLiteral("latitude"))
                    statement(BALD.arrayFirstValue, createTypedLiteral("6.5", XSDDatatype.XSDfloat))
                    statement(BALD.arrayLastValue, createTypedLiteral("-6.5", XSDDatatype.XSDfloat))
                    statement(BALD.shape) {
                        list(15)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/lon")) {
                    statement(RDF.type, BALD.Array)
                    statement(RDFS.label, createPlainLiteral("longitude"))
                    statement(BALD.arrayFirstValue, createTypedLiteral("0.5", XSDDatatype.XSDfloat))
                    statement(BALD.arrayLastValue, createTypedLiteral("9.5", XSDDatatype.XSDfloat))
                    statement(BALD.shape) {
                        list(10)
                    }
                }
            }
        }
    }

    @Test
    fun run_withVariableReferences_outputsVariableReferences() {
        val inputFile = writeToNetCdf("/netcdf/ref-attr.cdl")
        val outputFile = createTempFile()
        val aliasFile = ResourceFileConverter.toFile("/turtle/var-alias.ttl", "ttl")

        run(
            "--uri", "http://test.binary-array-ld.net/example",
            "--alias", aliasFile.absolutePath,
            inputFile.absolutePath,
            outputFile.absolutePath
        )

        val model = createDefaultModel().read(outputFile.toURI().toString(), "ttl")
        ModelVerifier(model).apply {
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/var0"), sortAnon = ::sortRefs) {
                    statement(createProperty("${TestVocab.prefix}name"), createStringLiteral("var0"))
                    statement(TestVocab.references, createResource("http://test.binary-array-ld.net/example/var1"))
                    statement(TestVocab.references, createResource("http://test.binary-array-ld.net/example/var2"))
                    statement(RDF.type, BALD.Array)
                    statement(BALD.references) {
                        statement(RDF.type, BALD.Reference)
                        statement(BALD.sourceRefShape) {
                            list(10, 90, 15, 1)
                        }
                        statement(BALD.target, createResource("http://test.binary-array-ld.net/example/var1"))
                        statement(BALD.targetRefShape) {
                            list(1, 90, 15, 60)
                        }
                    }
                    statement(BALD.references) {
                        statement(RDF.type, BALD.Reference)
                        statement(BALD.sourceRefShape) {
                            list(10, 90, 15)
                        }
                        statement(BALD.target, createResource("http://test.binary-array-ld.net/example/var2"))
                        statement(BALD.targetRefShape) {
                            list(1, 1, 15)
                        }
                    }
                    statement(BALD.shape) {
                        list(10, 90, 15)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/var1")) {
                    statement(TestVocab.references, createResource("http://test.binary-array-ld.net/example/var2"))
                    statement(RDF.type, BALD.Array)
                    statement(BALD.references) {
                        statement(RDF.type, BALD.Reference)
                        statement(BALD.sourceRefShape) {
                            list(90, 15, 60)
                        }
                        statement(BALD.target, createResource("http://test.binary-array-ld.net/example/var2"))
                        statement(BALD.targetRefShape) {
                            list(1, 15, 1)
                        }
                    }
                    statement(BALD.shape) {
                        list(90, 15, 60)
                    }
                }
                statement(BALD.contains, createResource("http://test.binary-array-ld.net/example/var2")) {
                    statement(RDF.type, BALD.Array)
                    statement(BALD.shape) {
                        list(15)
                    }
                }
            }
        }
    }

    private fun sortRefs(res: Resource): String {
        return if (res.hasProperty(BALD.target)) {
            res.getProperty(BALD.target).`object`.toString()
        } else {
            res.id.toString()
        }
    }

    private fun StatementsVerifier.format() {
        statement(DCTerms.format) {
            statement(DCTerms.identifier, createResource("http://vocab.nerc.ac.uk/collection/M01/current/NC/"))
            statement(RDF.type, DCTerms.MediaType)
        }
    }

    private fun StatementsVerifier.distribution(downloadUrl: String? = null) {
        statement(DCAT.distribution) {
            statement(RDF.type, DCAT.Distribution)
            if (downloadUrl != null) statement(DCAT.downloadURL, createResource(downloadUrl))
            statement(DCAT.mediaType) {
                statement(DCTerms.identifier, createStringLiteral("application/x-netcdf"))
                statement(RDF.type, DCTerms.MediaType)
            }
        }
    }
}