package net.bald.model

import bald.model.ModelVerifier
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import net.bald.BinaryArray
import net.bald.Container
import net.bald.Var
import net.bald.vocab.BALD
import org.apache.jena.rdf.model.Model
import org.apache.jena.shared.PrefixMapping
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
        }
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
            .setNsPrefix("dct", DCTerms.NS)
        val ba = mock<BinaryArray> {
            on { this.uri } doReturn uri
            on { this.root } doReturn root
            on { prefixMapping } doReturn prefix
        }

        val model = convert(ba)

        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
            prefix("dct", DCTerms.NS)
            resource("http://test.binary-array-ld.net/example") {
                statement(RDF.type, BALD.Container)
                statement(BALD.contains, model.createResource("http://test.binary-array-ld.net/example/")) {
                    statement(RDF.type, BALD.Container)
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
}