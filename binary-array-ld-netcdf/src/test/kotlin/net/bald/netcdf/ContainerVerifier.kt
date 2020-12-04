package net.bald.netcdf

import net.bald.Container
import net.bald.Var
import kotlin.test.fail

/**
 * TODO
 */
class ContainerVerifier(
    private val container: Container
): AttributeSourceVerifier(container) {
    fun vars(verify: VarsVerifier.() -> Unit) {
        val vars = container.vars().sortedBy(Var::uri)
        val varIt = vars.iterator()
        VarsVerifier(container, varIt).verify()
        if (varIt.hasNext()) {
            fail("Unexpected variable on ${container}: ${varIt.next()}")
        }
    }

    fun subContainers(verify: ContainersVerifier.() -> Unit) {
        val containers = container.subContainers()
        val containerIt = containers.iterator()
        ContainersVerifier(container, containerIt).verify()
        if (containerIt.hasNext()) {
            fail("Unexpected sub-container on ${container}: ${containerIt.next()}")
        }
    }
}