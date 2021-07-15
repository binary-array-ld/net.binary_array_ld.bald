package net.bald.model

import bald.model.ModelVerifier
import bald.model.StatementsVerifier
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import net.bald.*
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.DCAT
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.SKOS
import org.junit.jupiter.api.*

/**
 * Test the full Binary Array -> Linked Data conversion process using a mock binary array.
 */
class ModelBinaryArrayConverterTest {

    private fun convert(ba: BinaryArray): Model {
        return ModelBinaryArrayConverter.convert(ba)
    }

    private fun newVar(uri: String): Var {
        return mock {
            on { this.uri } doReturn uri
            on { dimensions() } doReturn emptySequence()
            on { attributes() } doReturn emptySequence()
            on { references() } doReturn emptySequence()
        }
    }

    @Test
    fun convert_returnsModel() {
        val uri = "http://test.binary-array-ld.net/example"
        val vars = listOf(newVar("$uri/foo"), newVar("$uri/bar"), newVar("$uri/baz"))
        val root = mock<Container> {
            on { this.uri } doReturn "$uri/"
            on { vars() } doReturn vars.asSequence()
            on { subContainers() } doReturn emptySequence()
            on { attributes() } doReturn emptySequence()
        }
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
        val format = mock<Format> {
            on { identifier } doReturn ResourceFactory.createResource("http://vocab.nerc.ac.uk/collection/M01/current/NC/")
        }
        val distribution = mock<Distribution> {
            on { mediaType } doReturn "application/x-netcdf"
        }
        val ba = mock<BinaryArray> {
            on { this.root } doReturn root
            on { prefixMapping } doReturn prefix
            on { this.format } doReturn format
            on { this.distribution } doReturn distribution
        }

        val model = convert(ba)

        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("dct", DCTerms.NS)
            resource("http://test.binary-array-ld.net/example/") {
                format()
                statement(RDF.type, BALD.Container)
                distribution()
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/bar")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/baz")) {
                    statement(RDF.type, BALD.Resource)
                }
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/foo")) {
                    statement(RDF.type, BALD.Resource)
                }
            }
        }
    }
}