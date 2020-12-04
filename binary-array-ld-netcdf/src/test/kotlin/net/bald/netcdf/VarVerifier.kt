package net.bald.netcdf

import net.bald.Var

/**
 * Test utility for verifying the characteristics of a [Var].
 * @param v The variable to verify.
 */
class VarVerifier(
    v: Var
): AttributeSourceVerifier(v)