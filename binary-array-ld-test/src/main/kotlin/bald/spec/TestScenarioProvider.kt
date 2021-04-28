package bald.spec

import bald.json.JsonTestSpec
import bald.json.JsonUtil
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * JUnit arguments provider for deserializing [SpecRequirement] instances.
 * The test suite is defined in the /spec/spec.yaml resource.
 * Tests are defined in [JsonTestSpec] and [JsonTestScenario] format.
 * @see BaseSpecTest
 */
class TestScenarioProvider: ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return suite().tests().map { test -> Arguments.of(test) }.asStream()
    }

    private fun suite(): TestSpec {
        return javaClass.getResourceAsStream("/spec/spec.yaml").use { input ->
            JsonUtil.mapper.readValue(input, JsonTestSpec::class.java)
        }
    }
}