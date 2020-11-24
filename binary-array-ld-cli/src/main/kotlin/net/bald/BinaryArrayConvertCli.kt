package net.bald

import net.bald.alias.AliasBinaryArray
import net.bald.context.ContextBinaryArray
import net.bald.model.ModelAliasDefinition
import net.bald.model.ModelBinaryArrayConverter
import net.bald.netcdf.NetCdfBinaryArray
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.jena.rdf.model.ModelFactory
import java.io.File
import java.io.OutputStream
import kotlin.system.exitProcess

/**
 * Command Line Interface for converting NetCDF metadata to Linked Data graphs.
 */
class BinaryArrayConvertCli {
    private val opts = Options().apply {
        addOption("u", "uri", true, "The URI which identifies the dataset.")
        addOption("a", "alias", true, "Comma-delimited list of RDF alias files.")
        addOption("c", "context", true, "Comma-delimited list of JSON-LD context files.")
        addOption("h", "help", false, "Show help.")
    }

    fun run(vararg args: String) {
        val cmdOpts = options(opts, *args)
        if (cmdOpts.help) {
            help()
        } else {
            try {
                doRun(cmdOpts)
            } catch (e: Exception) {
                help()
                throw e
            }
        }
    }

    private fun doRun(opts: CommandLineOptions) {
        val inputLoc = opts.inputLoc ?: throw IllegalArgumentException("First argument is required: NetCDF file to convert.")
        val ba = NetCdfBinaryArray.create(inputLoc, opts.uri)
            .withContext(opts.contextLocs)
            .withAlias(opts.aliasLocs)
        val model = ba.use(ModelBinaryArrayConverter::convert)

        modelOutput(opts.outputLoc).use { output ->
            model.write(output, "ttl")
        }
    }

    private fun BinaryArray.withContext(contextLocs: List<String>): BinaryArray {
        val contexts = contextLocs.map { contextLoc ->
            ModelFactory.createDefaultModel().read(contextLoc, "json-ld")
        }
        return ContextBinaryArray.create(this, contexts)
    }

    private fun BinaryArray.withAlias(aliasLocs: List<String>): BinaryArray {
        return if (aliasLocs.isEmpty()) {
            this
        } else {
            val alias = ModelFactory.createDefaultModel().apply {
                aliasLocs.forEach(::read)
            }.let(ModelAliasDefinition::create)
            AliasBinaryArray.create(this, alias)
        }
    }

    private fun options(opts: Options, vararg args: String): CommandLineOptions {
        return DefaultParser().parse(opts, args).let(::CommandLineOptions)
    }

    private fun help() {
        HelpFormatter().printHelp("[options] inputFile [outputFile]", opts)
    }

    private fun modelOutput(outputLoc: String?): OutputStream {
        return outputLoc?.let(::File)?.outputStream() ?: System.out
    }
}

fun main(args: Array<String>) {
    try {
        BinaryArrayConvertCli().run(*args)
    } catch (e: Exception) {
        println("Conversion failed due to error: ${e.message}")
        exitProcess(1)
    }
}