package net.bald.netcdf

import bald.netcdf.CdlConverter.writeToNetCdf
import net.bald.BinaryArray
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NetCdfBinaryArrayTest {

    private fun fromCdl(cdlLoc: String, uri: String? = null): BinaryArray {
        val file = writeToNetCdf(cdlLoc)
        return NetCdfBinaryArray.create(file.absolutePath, uri)
    }

    @Test
    fun uri_withUri_returnsValue() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)
        assertEquals(uri, ba.uri)
    }

    @Test
    fun uri_withoutUri_returnsFileUri() {
        val netCdfFile = writeToNetCdf("/netcdf/identity.cdl")
        val ba = NetCdfBinaryArray.create(netCdfFile.absolutePath)
        val expectedUri = netCdfFile.toPath().toUri().toString()
        assertEquals(expectedUri, ba.uri)
    }

    @Test
    fun root_vars_withVars_returnsVariables() {
        val uri = "http://test.binary-array-ld.net/identity.nc"
        val ba = fromCdl("/netcdf/identity.cdl", uri)

        val vars = ba.root.vars().toList()
        assertEquals(2, vars.size)
        assertEquals("var0", vars[0].name)
        assertEquals("var1", vars[1].name)
    }

    @Test
    fun prefixMapping_withoutPrefixMapping_returnsEmptyPrefixMapping() {
        val ba = fromCdl("/netcdf/identity.cdl", "http://test.binary-array-ld.net/prefix.nc")
        assertEquals(emptyMap(), ba.prefixMapping.nsPrefixMap)
    }

    @Test
    fun prefixMapping_withInternalPrefixMapping_returnsPrefixMapping() {
        val ba = fromCdl("/netcdf/prefix.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val prefix = ba.prefixMapping.nsPrefixMap
        val expected = mapOf(
            "bald" to BALD.prefix,
            "skos" to SKOS.uri
        )
        assertEquals(expected, prefix)
    }

    @Test
    fun prefixMapping_prefixGroupDoesNotExist_throwsException() {
        val ba = fromCdl("/netcdf/prefix-group-error.cdl", "http://test.binary-array-ld.net/prefix.nc")
        val ise = assertThrows<java.lang.IllegalStateException> {
            ba.prefixMapping
        }
        assertEquals("Prefix group not_prefix_list not found.", ise.message)
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
    fun attributes_withAttributes_returnsRootGroupAttributes() {
        val ba = fromCdl("/netcdf/attributes.cdl", "http://test.binary-array-ld.net/attributes.nc")
        val root = ba.root
        val prefix = org.apache.jena.shared.PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)

        AttributeSourceVerifier(root).attributes(prefix) {
            attribute(BALD.isPrefixedBy.uri, "bald__isPrefixedBy") {
                value(createPlainLiteral("prefix_list"))
            }
            attribute(SKOS.prefLabel.uri, "skos__prefLabel") {
                value(createPlainLiteral("Attributes metadata example"))
            }
            attribute(DCTerms.publisher.uri, "dct__publisher") {
                value(createResource("${BALD.prefix}Organisation"))
            }
            attribute(null, "date") {
                value(createPlainLiteral("2020-10-29"))
            }
        }
    }

    @Test
    fun attributes_withAttributes_returnsVarAttributes() {
        val ba = fromCdl("/netcdf/attributes.cdl", "http://test.binary-array-ld.net/attributes.nc")
        val vars = ba.root.vars().sortedBy(Var::toString).toList()
        val prefix = org.apache.jena.shared.PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
            .setNsPrefix("rdf", RDF.uri)

        assertEquals(2, vars.size)
        AttributeSourceVerifier(vars[0]).attributes(prefix) {
            attribute(RDF.type.uri, "rdf__type") {
                value(BALD.Array)
            }
            attribute(SKOS.prefLabel.uri, "skos__prefLabel") {
                value(createPlainLiteral("Variable 0"))
            }
        }
        AttributeSourceVerifier(vars[1]).attributes(prefix) {
            // none
        }
    }
}