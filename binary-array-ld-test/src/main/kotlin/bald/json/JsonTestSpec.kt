package bald.json

import bald.spec.SpecRequirement
import bald.spec.TestSpec
import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class JsonTestSpec(
    private val tests: List<JsonSpecRequirement>
): TestSpec {
    override fun tests(): Sequence<SpecRequirement> {
        return tests.asSequence()
    }
}