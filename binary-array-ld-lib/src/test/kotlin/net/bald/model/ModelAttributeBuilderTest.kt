package net.bald.model

import bald.model.ResourceVerifier
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import net.bald.Attribute
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory.*
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import org.junit.jupiter.api.*

class ModelAttributeBuilderTest {
    private val uri = "http://test.binary-array-ld.net/example/var0"
    private val resource = ModelFactory.createDefaultModel().createResource(uri)
    private val attr = mock<Attribute> {
        on { uri } doReturn RDFS.label.uri
    }
    private val builder = ModelAttributeBuilder.Factory().forResource(resource)

    @Test
    fun addAttribute_withoutValues_doesNothing() {
        attr.stub {
            on { values } doReturn emptyList()
        }
        builder.addAttribute(attr)
        ResourceVerifier(resource).statements {
            // none
        }
    }

    @Test
    fun addAttribute_singleValue_addsStatement() {
        val value = createPlainLiteral("Variable 0")
        attr.stub {
            on { values } doReturn listOf(value)
        }
        builder.addAttribute(attr)
        ResourceVerifier(resource).statements {
            statement(RDFS.label, value)
        }
    }

    @Test
    fun addAttribute_multipleValues_addsStatements() {
        val values = listOf(
            createPlainLiteral("Var 0"),
            createPlainLiteral("Variable 0")
        )
        attr.stub {
            on { this.values } doReturn values
        }
        builder.addAttribute(attr)
        ResourceVerifier(resource).statements {
            statement(RDFS.label, values[0])
            statement(RDFS.label, values[1])
        }
    }

    @Test
    fun addAttribute_resourceValue_addsStatement() {
        val value = createResource("http://test.binary-array-ld.net/label")
        attr.stub {
            on { values } doReturn listOf(value)
        }
        builder.addAttribute(attr)
        ResourceVerifier(resource).statements {
            statement(RDFS.label, value)
        }
    }

    @Test
    fun addAttribute_rdfListValue_addsList() {
        val value = ModelFactory.createDefaultModel().createList(
            createResource("http://test.binary-array-ld.net/var0"),
            createResource("http://test.binary-array-ld.net/var1"),
            createResource("http://test.binary-array-ld.net/var2")
        )
        attr.stub {
            on { values } doReturn listOf(value)
        }
        builder.addAttribute(attr)
        ResourceVerifier(resource).statements {
            statement(RDFS.label) {
                statement(RDF.first, createResource("http://test.binary-array-ld.net/var0"))
                statement(RDF.rest) {
                    statement(RDF.first, createResource("http://test.binary-array-ld.net/var1"))
                    statement(RDF.rest) {
                        statement(RDF.first, createResource("http://test.binary-array-ld.net/var2"))
                        statement(RDF.rest, RDF.nil)
                    }
                }
            }
        }
    }
}