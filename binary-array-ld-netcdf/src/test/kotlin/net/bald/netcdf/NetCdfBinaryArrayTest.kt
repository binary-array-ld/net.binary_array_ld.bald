package net.bald.netcdf

import bald.netcdf.CdlConverter.writeToNetCdf
import net.bald.BinaryArray
import net.bald.Var
import net.bald.Container
import net.bald.context.AliasDefinition
import net.bald.context.ModelContext
import net.bald.model.ModelAliasDefinition
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

    private fun fromCdl(cdlLoc: String, uri: String? = null, context: ModelContext? = null): BinaryArray {
        val file = writeToNetCdf(cdlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri, context)
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

        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("$uri/var0", vars[0].uri)
        assertEquals("$uri/var1", vars[1].uri)
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
        val groups = root.subContainers().sortedBy(Container::uri).toList()
        assertEquals(2, groups.size)

        val group0 = groups[0]
        val group0Vars = group0.vars().toList()
        assertEquals("$uri/group0", group0.uri)
        assertEquals(2, group0Vars.size)
        assertEquals("$uri/group0/var2", group0Vars[0].uri)
        assertEquals("$uri/group0/var3", group0Vars[1].uri)

        val group1 = groups[1]
        val group1Vars = group1.vars().toList()
        assertEquals("$uri/group1", group1.uri)
        assertEquals(2, group1Vars.size)
        assertEquals("$uri/group1/var4", group1Vars[0].uri)
        assertEquals("$uri/group1/var5", group1Vars[1].uri)
    }

    @Test
    fun root_subContainers_withInternalPrefixMappingGroup_excludesPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/prefix.nc")
        assertEquals(emptyList(), ba.root.subContainers().toList())
    }

    @Test
    fun root_vars_withInternalPrefixMappingVar_excludesPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/prefix-var.nc")
        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("http://test.binary-array-ld.net/prefix-var.nc/var0", vars[0].uri)
        assertEquals("http://test.binary-array-ld.net/prefix-var.nc/var1", vars[1].uri)
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
        val root = ba.root

        AttributeSourceVerifier(root).attributes {
            attribute(BALD.isPrefixedBy.uri) {
                value(createPlainLiteral("prefix_list"))
            }
            attribute(SKOS.prefLabel.uri) {
                value(createPlainLiteral("Attributes metadata example"))
            }
            attribute(DCTerms.publisher.uri) {
                value(createResource("${BALD.prefix}Organisation"))
            }
            attribute("http://test.binary-array-ld.net/attributes.nc/date") {
                value(createPlainLiteral("2020-10-29"))
            }
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
        val vars = ba.root.vars().sortedBy(Var::toString).toList()

        assertEquals(2, vars.size)
        AttributeSourceVerifier(vars[0]).attributes {
            attribute(RDF.type.uri) {
                value(BALD.Array)
            }
            attribute(SKOS.prefLabel.uri) {
                value(createPlainLiteral("Variable 0"))
            }
        }
        AttributeSourceVerifier(vars[1]).attributes {
            // none
        }
    }

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
        val ctx = ModelContext.create(prefix, alias)
        val ba = fromCdl("/netcdf/alias.cdl", "http://test.binary-array-ld.net/alias.nc", ctx)
        val root = ba.root
        AttributeSourceVerifier(root).attributes {
            attribute(BALD.isPrefixedBy.uri) {
                value(createPlainLiteral("prefix_list"))
            }
            attribute(SKOS.prefLabel.uri) {
                value(createPlainLiteral("Alias metadata example"))
            }
            attribute(DCTerms.publisher.uri) {
                value(createResource("${BALD.prefix}Organisation"))
            }
            attribute("http://test.binary-array-ld.net/alias.nc/date") {
                value(createPlainLiteral("2020-10-29"))
            }
        }

        val vars = root.vars().sortedBy(Var::toString).toList()
        assertEquals(2, vars.size)
        AttributeSourceVerifier(vars[0]).attributes {
            attribute(RDFS.label.uri) {
                value(createPlainLiteral("var-0"))
            }
            attribute(RDF.type.uri) {
                value(BALD.Array)
            }
            attribute(SKOS.prefLabel.uri) {
                value(createPlainLiteral("Variable 0"))
            }
        }
    }
}