package bald.spec

/**
 * A suite of tests which implement the BALD specification.
 */
interface TestSpec {
    /**
     * List the tests.
     * @return The tests.
     */
    fun tests(): Sequence<SpecRequirement>
}