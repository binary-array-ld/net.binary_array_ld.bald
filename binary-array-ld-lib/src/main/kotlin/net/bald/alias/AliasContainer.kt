package net.bald.alias

import net.bald.Container
import net.bald.Var

/**
 * Decorator for [Container] which supports attribute aliasing.
 */
class AliasContainer(
    private val container: Container,
    private val alias: AliasDefinition
): AliasAttributeSource(container, alias), Container {
    override val uri: String get() = container.uri

    override fun vars(): Sequence<Var> {
        return container.vars().map { v ->
            AliasVar(v, alias)
        }
    }

    override fun subContainers(): Sequence<Container> {
        return container.subContainers().map { container ->
            AliasContainer(container, alias)
        }
    }
}