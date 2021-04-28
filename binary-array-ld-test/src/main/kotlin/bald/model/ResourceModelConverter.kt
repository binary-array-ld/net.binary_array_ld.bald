package bald.model

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory

object ResourceModelConverter {
    fun toModel(modelLoc: String): Model {
        return javaClass.getResourceAsStream(modelLoc).use { input ->
            ModelFactory.createDefaultModel().read(input, null, "ttl")
        }
    }
}