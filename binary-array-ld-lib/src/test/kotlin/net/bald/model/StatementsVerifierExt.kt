package net.bald.model

import bald.model.StatementsVerifier
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.vocabulary.DCAT
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF

fun StatementsVerifier.format() {
    statement(DCTerms.format) {
        statement(DCTerms.identifier, createResource("http://vocab.nerc.ac.uk/collection/M01/current/NC/"))
        statement(RDF.type, DCTerms.MediaType)
    }
}

fun StatementsVerifier.distribution(downloadUrl: String? = null) {
    statement(DCAT.distribution) {
        statement(RDF.type, DCAT.Distribution)
        if (downloadUrl != null) statement(DCAT.downloadURL, createResource(downloadUrl))
        statement(DCAT.mediaType) {
            statement(DCTerms.identifier, ResourceFactory.createStringLiteral("application/x-netcdf"))
            statement(RDF.type, DCTerms.MediaType)
        }
    }
}