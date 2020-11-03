package net.bald.model

import org.apache.jena.rdf.model.Resource

fun Resource.withTrailingSlash(): String {
    return if (uri.endsWith('/')) uri else "$uri/"
}