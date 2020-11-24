package net.bald.alias

import net.bald.Attribute
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.ResourceFactory

/**
 * Decorator for [Attribute] which supports attribute aliasing.
 */
class AliasAttribute(
    private val attr: Attribute,
    private val alias: AliasDefinition
): Attribute {
    override val uri: String? get() = attr.uri ?: alias.property(attr.name)?.uri
    override val name: String get() = attr.name

    override val values: List<RDFNode> get() {
        return attr.values.map { value ->
            if (value.isLiteral) {
                val raw = value.asLiteral().lexicalForm
                alias.resource(raw) ?: value
            } else {
                value
            }
        }
    }
}