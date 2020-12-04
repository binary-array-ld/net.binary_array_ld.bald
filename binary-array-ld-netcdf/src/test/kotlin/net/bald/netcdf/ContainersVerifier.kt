package net.bald.netcdf

import net.bald.Container
import kotlin.test.assertEquals
import kotlin.test.fail

class ContainersVerifier(
    private val parent: Container,
    private val containerIt: Iterator<Container>
) {
    /**
     * Verify that the next container has the given URI.
     * Begin verifying the characteristics of the next container.
     * @param uri The expected container URI.
     * @param verify A function to perform against the [ContainerVerifier] for the container.
     */
    fun container(uri: String, verify: ContainerVerifier.() -> Unit = {}) {
        if (containerIt.hasNext()) {
            val container = containerIt.next()
            assertEquals(uri, container.uri, "Wrong URI on container $container.")
            ContainerVerifier(container).verify()
        } else {
            fail("Expected container with URI $uri on $parent, but no more containers were found.")
        }
    }
}