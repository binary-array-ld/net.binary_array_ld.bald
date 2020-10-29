package net.bald.model

import net.bald.BinaryArray
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.shared.PrefixMapping

/**
 * API for converting a [BinaryArray] to a linked data [Model].
 */
object ModelBinaryArrayConverter {
    private val modelFct = run {
        val attrFct = ModelAttributeBuilder.Factory()
        val varFct = ModelVarBuilder.Factory(attrFct)
        val containerFct = ModelContainerBuilder.Factory(varFct, attrFct)
        ModelBinaryArrayBuilder.Factory(containerFct)
    }

    /**
     * Convert the given binary array metadata into a linked data model.
     * @param ba The binary array to convert.
     * @param prefixes An external prefix mapping to apply to the graph.
     * @return The resulting model.
     */
    @JvmStatic
    fun convert(ba: BinaryArray, prefixes: PrefixMapping): Model {
        val model = ModelFactory.createDefaultModel().setNsPrefixes(prefixes)
        modelFct.forModel(model).addBinaryArray(ba)
        return model
    }

    /**
     * @see [convert].
     */
    @JvmStatic
    fun convert(ba: BinaryArray): Model {
        val prefixes = PrefixMapping.Factory.create()
        return convert(ba, prefixes)
    }
}