package net.bald.alias

import net.bald.Var

/**
 * Decorator for [Var] which supports attribute aliasing.
 */
class AliasVar(
    private val v: Var,
    alias: AliasDefinition
): AliasAttributeSource(v, alias), Var {
    override val name: String get() = v.name
}