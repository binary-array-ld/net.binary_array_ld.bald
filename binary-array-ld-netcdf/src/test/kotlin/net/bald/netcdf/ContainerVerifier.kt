package net.bald.netcdf

import net.bald.Container
import net.bald.Var
import kotlin.test.fail

/**
 * Test utility for verifying the characteristics of a [Container].
 * @param container The container to verify.
 */
class ContainerVerifier(
    private val container: Container
): AttributeSourceVerifier(container) {
    /**
     * Begin verifying the variables in the container.
     * Variables are supplied in alphabetical order of their URIs.
     * Also verify that the number of variables verified is equal to the total number of variables available.
     * @param verify A function to perform against the [VarsVerifier] for the container.
     */
    fun vars(verify: VarsVerifier.() -> Unit) {
        val vars = container.vars().sortedBy(Var::uri)
        val varIt = vars.iterator()
        VarsVerifier(container, varIt).verify()
        if (varIt.hasNext()) {
            fail("Unexpected variable on ${container}: ${varIt.next()}")
        }
    }

    /**
     * Begin verifying the sub-containers in the container.
     * Sub-containers are supplied in the order they are declared.
     * Also verify that the number of sub-containers verified is equal to the total number of sub-containers available.
     * @param verify A function to perform against the [ContainersVerifier] for the container.
     */
    fun subContainers(verify: ContainersVerifier.() -> Unit) {
        val containers = container.subContainers()
        val containerIt = containers.iterator()
        ContainersVerifier(container, containerIt).verify()
        if (containerIt.hasNext()) {
            fail("Unexpected sub-container on ${container}: ${containerIt.next()}")
        }
    }
}