package net.bald.netcdf

import net.bald.Container
import net.bald.Var
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * TODO
 */
class VarsVerifier(
    private val container: Container,
    private val varIt: Iterator<Var>
) {
    fun variable(uri: String, verify: VarVerifier.() -> Unit = {}) {
        if (varIt.hasNext()) {
            val v = varIt.next()
            assertEquals(uri, v.uri, "Wrong URI on variable $v.")
            VarVerifier(v).verify()
        } else {
            fail("Expected variable with URI $uri on $container, but no more variables were found.")
        }
    }
}