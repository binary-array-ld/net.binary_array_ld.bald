package bald.spec

import net.bald.Converter
import org.apache.jena.rdf.model.Model

/**
 * A scenario which tests a specific requirement in the BALD specification.
 */
interface SpecRequirement {
    /**
     * The name of the requirement.
     */
    val name: String

    /**
     * A description of the requirement.
     */
    val comment: String?

    /**
     * Converts this test's input files and parameters into a graph using the given converter.
     * @param converter The converter implementation to test.
     * @return The graph resulting from the conversion.
     */
    fun result(converter: Converter): Model

    /**
     * Obtain the expected RDF graph.
     * @return The expected graph result.
     */
    fun expectation(): Model
}