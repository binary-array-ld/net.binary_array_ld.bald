package net.bald

import net.bald.context.ModelContext
import net.bald.model.ModelAliasDefinition
import net.bald.model.ModelBinaryArrayConverter
import net.bald.netcdf.NetCdfBinaryArray
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.jena.rdf.model.ModelFactory
import java.io.*
import kotlin.system.exitProcess

/**
 * Command Line Interface for converting NetCDF metadata to Linked Data graphs.
 */
class BinaryArrayConvertCli {
    private val opts = Options().apply {
        addOption("u", "uri", true, "The URI which identifies the dataset.")
        addOption("a", "alias", true, "Comma-delimited list of RDF alias files.")
        addOption("c", "context", true, "Comma-delimited list of JSON-LD context files.")
        addOption("o", "output", true, "Output format. eg. ttl, json-ld, rdfxml.")
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
        val context = context(opts.contextLocs, opts.aliasLocs)
        val inputLoc = opts.inputLoc ?: throw IllegalArgumentException("First argument is required: NetCDF file to convert.")
        val ba = NetCdfBinaryArray.create(inputLoc, opts.uri, context)
        val model = ba.use(ModelBinaryArrayConverter::convert)
        val outputFormat = opts.outputFormat ?: "ttl"

        modelOutput(opts.outputLoc).use { output ->
            model.write(output, outputFormat)
        }
    }

    private fun context(contextLocs: List<String>, aliasLocs: List<String>): ModelContext {
        val prefixes = contextLocs.map { contextLoc ->
            ModelFactory.createDefaultModel().read(contextLoc, "json-ld")
        }
        val alias = ModelFactory.createDefaultModel().apply {
            aliasLocs.forEach(::read)
        }.let(ModelAliasDefinition::create)

        return ModelContext.create(prefixes, alias)
    }

    private fun options(opts: Options, vararg args: String): CommandLineOptions {
        return DefaultParser().parse(opts, args).let(::CommandLineOptions)
    }

    private fun help() {
        HelpFormatter().printHelp("[options] inputFile [outputFile]", opts)
    }

    private fun modelOutput(outputLoc: String?): OutputStream {
        return outputLoc?.let(::File)?.outputStream() ?: object: FilterOutputStream(System.out) {
            override fun close() {
                // do nothing, leave System.out open
            }
        }
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
