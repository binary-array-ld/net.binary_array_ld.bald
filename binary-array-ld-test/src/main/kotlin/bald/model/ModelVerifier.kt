package bald.model

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test utility for verifying statements about resources in a given model.
 * @param model The model to verify.
 */
class ModelVerifier(
    private val model: Model
) {
    /**
     * Verify that a resource with the given URI appears in at least one statement in the model.
     * Then, begin verifying statements about that resource in the model.
     * @param uri The URI of the resource to verify.
     * @param verify A function to perform against the [StatementsVerifier] for the resource.
     */
    fun resource(uri: String, verify: StatementsVerifier.() -> Unit) {
        val resource = model.createResource(uri)
        assertTrue(model.containsResource(resource), "Expected resource $resource in model.")
        ResourceVerifier(resource).statements(verify = verify)
    }

    /**
     * Verify that the given prefix name is associated with the given URI.
     * @param prefix The prefix name.
     * @param uri The prefix URI.
     */
    fun prefix(prefix: String, uri: String) {
        assertEquals(uri, model.getNsPrefixURI(prefix), "Expected prefix mapping for $prefix.")
    }
}