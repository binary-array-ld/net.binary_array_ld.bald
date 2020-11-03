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

    private fun newVar(name: String): Var {
        return mock {
            on { this.name } doReturn name
        }
    }

    @Test
    fun convert_returnsModel() {
        val vars = listOf(newVar("foo"), newVar("bar"), newVar("baz"))
        val root = mock<Container> {
            on { vars() } doReturn vars.asSequence()
            on { subContainers() } doReturn emptySequence()
        }
        val prefix = PrefixMapping.Factory.create()
            .setNsPrefix("bald", BALD.prefix)
            .setNsPrefix("skos", SKOS.uri)
        val ba = mock<BinaryArray> {
            on { uri } doReturn "http://test.binary-array-ld.net/example"
            on { this.root } doReturn root
            on { prefixMapping } doReturn prefix
        }
        val model = convert(ba)

        ModelVerifier(model).apply {
            prefix("bald", BALD.prefix)
            prefix("skos", SKOS.uri)
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