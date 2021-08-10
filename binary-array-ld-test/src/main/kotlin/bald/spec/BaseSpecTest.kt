package bald.spec

import bald.json.JsonSpecRequirement
import bald.json.JsonTestSpec
import net.bald.Converter
import org.apache.jena.rdf.model.Model
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.test.fail

/**
 * Base class for integration tests that run a converter against the BALD specification.
 * Runs a series of parameterised tests with parameters provided by [TestScenarioProvider].
 * The test suite is defined in the /spec/spec.yaml resource.
 * Tests are defined in [JsonTestSpec] and [JsonSpecRequirement] format.
 * To add or modify tests, update /spec/spec.yaml.
 */
open class BaseSpecTest(
    private val converter: Converter
) {
    @ParameterizedTest
    @ArgumentsSource(TestScenarioProvider::class)
    fun test(test: SpecRequirement) {
        println("Test ${test.name}:\n${test.comment ?: "No description"}")

        val result = test.result(converter)
        val expected = test.expectation()
        val success = expected.isIsomorphicWith(result)

        if (success) {
            println("Test ${test.name} passed.")
        } else {
            println("Test ${test.name} failed.")

            expected.diff(result)?.let { diff ->
                println("The following statements were expected but not found:")
                diff.write(System.out, "ttl")
            }

            result.diff(expected)?.let { diff ->
                println("The following statements were found but not expected:")
                diff.write(System.out, "ttl")
            }

            fail("Test ${test.name} failed - see log for details.")
        }
    }

    private fun Model.diff(to: Model): Model? {
        val prefixes = this.nsPrefixMap + to.nsPrefixMap
        return difference(to).setNsPrefixes(prefixes).takeUnless(Model::isEmpty)
    }
}