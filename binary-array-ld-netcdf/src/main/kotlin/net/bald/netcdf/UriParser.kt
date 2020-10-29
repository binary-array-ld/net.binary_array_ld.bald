package net.bald.netcdf

import org.apache.jena.shared.PrefixMapping

/**
 * Parser for URI or compact URI expressions in NetCDF attributes.
 */
class UriParser(
    private val prefixMap: PrefixMapping
) {
    /**
     * Parse the given value as a URI, if possible.
     * @param value The value to parse.
     * @return The inferred URI, if possible. Otherwise, null.
     */
    fun parse(value: String): String? {
        return if (value.contains("__")) {
            val (prefix, name) = value.split("__", limit = 2)
            prefixMap.getNsPrefixURI(prefix)?.plus(name)
        } else {
            null
        }
    }
}