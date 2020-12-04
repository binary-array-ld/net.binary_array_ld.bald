package bald.model

import org.apache.jena.rdf.model.*
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals

/**
 * Test utility for verifying a sequence of RDF statements.
 * @param statementIt The iterator for the sequence of statements.
 */
class StatementsVerifier(
    private val statementIt: Iterator<Statement>
) {
    /**
     * Verify that the next statement in the sequence has the given predicate and object.
     * Then, optionally begin verifying statements about the object resource.
     * To skip verification of the object resource, omit the [verifyResource] parameter.
     * @param prop The expected predicate.
     * @param value The expected resource object.
     * @param verifyResource A function to perform against the [StatementsVerifier] for the object resource.
     */
    fun statement(
        prop: Property,
        value: Resource,
        verifyResource: (StatementsVerifier.() -> Unit)? = null
    ) {
        nextStatement(prop) { statement ->
            assertEquals(prop, statement.predicate, "Wrong predicate on statement $statement.")
            val obj = statement.`object`
            if (obj.isResource) {
                val resource = obj.asResource()
                assertEquals(value.uri, resource.uri, "Wrong value on statement $statement.")
                if (verifyResource != null) {
                    ResourceVerifier(resource).statements(verifyResource)
                }
            } else {
                fail("Expected statement with resource value $value, but got $statement.")
            }
        }
    }

    /**
     * Verify that the next statement in the sequence has the given predicate and object.
     * @param prop The expected predicate.
     * @param value The expected literal object.
     */
    fun statement(
        prop: Property,
        value: Literal
    ) {
        nextStatement(prop) { statement ->
            assertEquals(prop, statement.predicate, "Wrong predicate on statement $statement.")
            assertEquals(value, statement.`object`, "Wrong value on statement $statement.")
        }
    }

    private fun nextStatement(prop: Property, verify: (Statement) -> Unit = {}) {
        return if (statementIt.hasNext()) {
            val statement = statementIt.next()
            verify(statement)
        } else {
            fail("Expected statement with property $prop, but no more statements were found.")
        }
    }
}

fun sortStatements(statements: Iterator<Statement>): Iterator<Statement> {
    return statements.asSequence().sortedBy(Statement::toString).iterator()
}