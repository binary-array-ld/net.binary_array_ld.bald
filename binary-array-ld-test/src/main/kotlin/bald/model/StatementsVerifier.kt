package bald.model

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.Statement
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
     * Then, begin verifying statements about the object resource.
     * @param prop The expected predicate.
     * @param value The expected object.
     * @param verifyResource A function to perform against the [StatementsVerifier] for the object resource.
     */
    fun statement(
        prop: Property,
        value: Resource,
        verifyResource: StatementsVerifier.() -> Unit = {}
    ) {
        return if (statementIt.hasNext()) {
            val statement = statementIt.next()
            assertEquals(prop, statement.predicate, "Wrong predicate on statement $statement.")

            val obj = statement.`object`
            if (obj.isResource) {
                val resource = obj.asResource()
                assertEquals(value.uri, resource.uri, "Wrong value on statement $statement.")
                ResourceVerifier(resource).statements(verifyResource)
            } else {
                fail("Expected statement with resource value $value, but got $statement.")
            }
        } else {
            fail("Expected statement with property $prop, but no more statements were found.")
        }
    }
}

fun sortStatements(statements: Iterator<Statement>): Iterator<Statement> {
    return statements.asSequence().sortedBy(Statement::toString).iterator()
}