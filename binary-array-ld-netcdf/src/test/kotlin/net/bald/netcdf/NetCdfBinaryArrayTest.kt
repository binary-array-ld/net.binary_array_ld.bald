package net.bald.netcdf

import bald.TestVocab
import bald.netcdf.CdlConverter.writeToNetCdf
import net.bald.BinaryArray
import net.bald.alias.AliasDefinition
import net.bald.context.ModelContext
import net.bald.alias.ModelAliasDefinition
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NetCdfBinaryArrayTest {

    private fun fromCdl(cdlLoc: String, uri: String? = null, context: ModelContext? = null, alias: AliasDefinition? = null): BinaryArray {
        val file = writeToNetCdf(cdlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri, context, alias)
    }

    /**
     * Requirements class A-1
     */
    @Test
    fun uri_withUri_returnsValue() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)
        assertEquals(uri, ba.uri)
    }

    /**
     * Requirements class A-1
     */
    @Test
    fun uri_withoutUri_returnsFileUri() {
        val netCdfFile = writeToNetCdf("/netcdf/identity.cdl")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        val expectedUri = netCdfFile.toPath().toUri().toString()
        assertEquals(expectedUri, ba.uri)
    }

    /**
     * Requirements class A-2
     */
    @Test
    fun root_vars_withVars_returnsVariables() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)

        ContainerVerifier(ba.root).vars {
            variable("$uri/var0")
            variable("$uri/var1")
        }
    }

    /**
     * Requirements class A-2
     */
    @Test
    fun root_subContainers_withSubgroups_returnsSubgroups() {
        val uri = "http://test.binary-array-ld.net/identity-subgroups.nc"
        val ba = fromCdl("/netcdf/identity-subgroups.cdl", uri)
        val root = ba.root
        assertEquals("$uri/", root.uri)
        ContainerVerifier(root).subContainers {
            container("$uri/group0") {
                vars {
                    variable("$uri/group0/var2")
                    variable("$uri/group0/var3")
                }
            }
            container("$uri/group1") {
                vars {
                    variable("$uri/group1/var4")
                    variable("$uri/group1/var5")
                }
            }
        }
    }

    @Test
    fun root_subContainers_withInternalPrefixMappingGroup_excludesPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/identity.nc")
        ContainerVerifier(ba.root).subContainers {
            // none
        }
    }

    @Test
    fun root_vars_withInternalPrefixMappingVar_excludesPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/prefix-var.nc")
        ContainerVerifier(ba.root).vars {
            variable("http://test.binary-array-ld.net/prefix-var.nc/var0")
            variable("http://test.binary-array-ld.net/prefix-var.nc/var1")
        }
    }

    @Test
    fun prefixMapping_withoutPrefixMapping_returnsEmptyPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/prefix.nc")
        assertEquals(emptyMap(), ba.prefixMapping.nsPrefixMap)
    }

    /**
     * Requirements class B-1
     */
    @Test
    fun prefixMapping_withInternalPrefixMappingGroup_returnsPrefixMapping() {
        val ba = fromCdl("/netcdf/prefix.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val prefix = ba.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri
        )
        assertEquals(expected, prefix)
    }

    /**
     * Requirements class B-1
     */
    @Test
    fun prefixMapping_withInternalPrefixMappingVar_returnsPrefixMapping() {
        val ba = fromCdl("/netcdf/prefix-var.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val prefix = ba.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri
        )
        assertEquals(expected, prefix)
    }

    /**
     * Requirements class B-1
     */
    @Test
    fun prefixMapping_prefixGroupDoesNotExist_throwsException() {
        val ba = fromCdl("/netcdf/prefix-group-error.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Prefix group or variable not_prefix_list not found.", ise.message)
    }

    @Test
    fun prefixMapping_prefixGroupAttrNonString_throwsException() {
        val ba = fromCdl("/netcdf/prefix-attr-error.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Global prefix attribute bald__isPrefixedBy must have a string value.", ise.message)
    }

    @Test
    fun prefixMapping_prefixUriNonString_throwsException() {
        val ba = fromCdl("/netcdf/prefix-uri-error.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Prefix attribute skos__ must have a string value.", ise.message)
    }

    /**
     * Requirements class B-8
     */
    @Test
    fun prefixMapping_withExternalPrefixMapping_returnsCombinedPrefixMapping() {
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("skos", "http://example.org/skos/")
            .setNsPrefix("dct", DCTerms.NS)
        val ctx = ModelContext.create(prefix)
        val ba = fromCdl("/netcdf/prefix.cdl", "http://test.binary-array-ld.net/prefix.nc", ctx)
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri,
            "dct" to DCTerms.NS
        )
        assertEquals(expected, ba.prefixMapping.nsPrefixMap)
    }

    /**
     * Requirements class D
     */
    @Test
    fun attributes_withAttributes_returnsRootGroupAttributes() {
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
        val ctx = ModelContext.create(prefix)
        val ba = fromCdl("/netcdf/attributes.cdl", "http://test.binary-array-ld.net/attributes.nc", ctx)
        ContainerVerifier(ba.root).attributes {
            attribute(BALD.isPrefixedBy.uri, createPlainLiteral("prefix_list"))
            attribute(SKOS.prefLabel.uri, createPlainLiteral("Attributes metadata example"))
            attribute(DCTerms.publisher.uri, createResource("${BALD.prefix}Organisation"))
            attribute("http://test.binary-array-ld.net/attributes.nc/date", createPlainLiteral("2020-10-29"))
        }
    }

    /**
     * Requirements class D
     */
    @Test
    fun attributes_withAttributes_returnsVarAttributes() {
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
            .setNsPrefix("rdf", RDF.uri)
        val ctx = ModelContext.create(prefix)
        val ba = fromCdl("/netcdf/attributes.cdl", "http://test.binary-array-ld.net/attributes.nc", ctx)
        ContainerVerifier(ba.root).vars {
            variable("http://test.binary-array-ld.net/attributes.nc/var0") {
                attributes {
                    attribute(RDF.type.uri, BALD.Array)
                    attribute(SKOS.prefLabel.uri, createPlainLiteral("Variable 0"))
                }
            }
            variable("http://test.binary-array-ld.net/attributes.nc/var1") {
                attributes {
                    // none
                }
            }
        }
    }

    /**
     * Requirements class C
     */
    @Test
    fun attributes_withAliases_returnsAliasedValues() {
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
            .setNsPrefix("rdf", RDF.uri)
        val alias = javaClass.getResourceAsStream("/turtle/alias.ttl").use { input ->
            ModelFactory.createDefaultModel().read(input, null, "ttl")
        }.let(ModelAliasDefinition::create)
        val ctx = ModelContext.create(prefix)
        val ba = fromCdl("/netcdf/alias.cdl", "http://test.binary-array-ld.net/alias.nc", ctx, alias)
        ContainerVerifier(ba.root).apply {
            attributes {
                attribute(BALD.isPrefixedBy.uri, createPlainLiteral("prefix_list"))
                attribute(SKOS.prefLabel.uri, createPlainLiteral("Alias metadata example"))
                attribute(DCTerms.publisher.uri, createResource("${BALD.prefix}Organisation"))
                attribute("http://test.binary-array-ld.net/alias.nc/date", createPlainLiteral("2020-10-29"))
            }
            vars {
                variable("http://test.binary-array-ld.net/alias.nc/var0") {
                    attributes {
                        attribute(RDFS.label.uri, createPlainLiteral("var-0"))
                        attribute(RDF.type.uri, BALD.Array)
                        attribute(SKOS.prefLabel.uri, createPlainLiteral("Variable 0"))
                    }
                }
                variable("http://test.binary-array-ld.net/alias.nc/var1")
            }
        }
    }

    /**
     * Requirements class E-1, E-2
     */
    @Test
    fun attributes_withVariableReferences_returnsVariableValues() {
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
            .setNsPrefix("rdf", RDF.uri)
        val alias = javaClass.getResourceAsStream("/turtle/var-alias.ttl").use { input ->
            ModelFactory.createDefaultModel().read(input, null, "ttl")
        }.let(ModelAliasDefinition::create)
        val ctx = ModelContext.create(prefix)
        val ba = fromCdl("/netcdf/var-ref.cdl", "http://test.binary-array-ld.net/var-ref.nc", ctx, alias)

        ContainerVerifier(ba.root).apply {
            attributes {
                attribute(BALD.isPrefixedBy.uri, createPlainLiteral("prefix_list"))
                attribute(SKOS.prefLabel.uri, createPlainLiteral("Variable reference metadata example"))
                attribute(TestVocab.rootVar.uri, createResource("http://test.binary-array-ld.net/var-ref.nc/var0"))
                attribute(TestVocab.unorderedVar.uri) {
                    value(createResource("http://test.binary-array-ld.net/var-ref.nc/var0"))
                    value(createResource("http://test.binary-array-ld.net/var-ref.nc/foo/var1"))
                    value(createResource("http://test.binary-array-ld.net/var-ref.nc/foo/bar/var2"))
                }
                attribute(TestVocab.orderedVar.uri) {
                    resource {
                        statements {
                            list(
                                createResource("http://test.binary-array-ld.net/var-ref.nc/var0"),
                                createResource("http://test.binary-array-ld.net/var-ref.nc/foo/bar/var2"),
                                createResource("http://test.binary-array-ld.net/var-ref.nc/baz/var3")
                            )
                        }
                    }
                }
            }
            vars {
                variable("http://test.binary-array-ld.net/var-ref.nc/var0")
            }
            subContainers {
                container("http://test.binary-array-ld.net/var-ref.nc/baz")
                container("http://test.binary-array-ld.net/var-ref.nc/foo") {
                    attributes {
                        attribute(TestVocab.rootVar.uri, createResource("http://test.binary-array-ld.net/var-ref.nc/var0"))
                        attribute(TestVocab.siblingVar.uri, createResource("http://test.binary-array-ld.net/var-ref.nc/baz/var3"))
                    }
                    vars {
                        variable("http://test.binary-array-ld.net/var-ref.nc/foo/var1") {
                            attributes {
                                attribute(BALD.references.uri, createPlainLiteral("var9"))
                                attribute(TestVocab.siblingVar.uri, createResource("http://test.binary-array-ld.net/var-ref.nc/foo/bar/var2"))
                            }
                        }
                    }
                    subContainers {
                        container("http://test.binary-array-ld.net/var-ref.nc/foo/bar") {
                            vars {
                                variable("http://test.binary-array-ld.net/var-ref.nc/foo/bar/var2") {
                                    attributes {
                                        attribute(TestVocab.parentVar.uri, createResource("http://test.binary-array-ld.net/var-ref.nc/foo/var1"))
                                        attribute(SKOS.prefLabel.uri, createPlainLiteral("var2"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun vars_range_withCoordinateVars_returnsCoordinateRange() {
        val ba = fromCdl("/netcdf/coordinate-var.cdl", "http://test.binary-array-ld.net/coordinate-var.nc")
        ContainerVerifier(ba.root).vars {
            variable("http://test.binary-array-ld.net/coordinate-var.nc/elev") {
                dimensions {
                    dimension {
                        size(15); coordinate("http://test.binary-array-ld.net/coordinate-var.nc/lat")
                    }
                    dimension {
                        size(10); coordinate("http://test.binary-array-ld.net/coordinate-var.nc/lon")
                    }
                }
            }
            variable("http://test.binary-array-ld.net/coordinate-var.nc/lat") {
                dimensions(15)
                range(6.5F, -6.5F)
            }
            variable("http://test.binary-array-ld.net/coordinate-var.nc/lon") {
                dimensions(10)
                range(0.5F, 9.5F)
            }
        }
    }
}