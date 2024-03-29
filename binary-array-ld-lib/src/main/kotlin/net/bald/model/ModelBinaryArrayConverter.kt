package net.bald.model

import net.bald.BinaryArray
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory

/**
 * API for converting a [BinaryArray] to a linked data [Model].
 */
object ModelBinaryArrayConverter {
    private val modelFct = run {
        val attrFct = ModelAttributeBuilder.Factory()
        val refFct = ModelReferenceBuilder.Factory()
        val varFct = ModelVarBuilder.Factory(attrFct, refFct)
        val containerFct = ModelContainerBuilder.Factory(varFct, attrFct)
        ModelBinaryArrayBuilder.Factory(containerFct)
    }

    /**
     * Convert the given binary array metadata into a linked data model.
     * @param ba The binary array to convert.
     * @return The resulting model.
     */
    @JvmStatic
    fun convert(ba: BinaryArray): Model {
        val model = ModelFactory.createDefaultModel()
        modelFct.forModel(model).addBinaryArray(ba)
        return model
    }
}