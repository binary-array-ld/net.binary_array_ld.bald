package bald.model

import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.Statement
import org.junit.jupiter.api.fail

/**
 * Test utility for verifying the statements about a specific resource in a model.
 * The model context will be derived from the given resource.
 * @param resource The resource to verify.
 */
class ResourceVerifier(
    private val resource: Resource
) {
    /**
     * Begin verifying statements about the resource.
     * Statements are sorted by subject, predicate, and object, and verification of statements must be done in order.
     * Also verify that the number of statements verified is equal to the total number of statements available.
     * @param verify A function to perform against the [StatementsVerifier] for the resource.
     */
    fun statements(verify: StatementsVerifier.() -> Unit) {
        val statementIt = resource.listProperties().let(::sortStatements)
        StatementsVerifier(statementIt).verify()

        if (statementIt.hasNext()) {
            fail("Unexpected statement on resource: ${statementIt.next()}")
        }
    }
}