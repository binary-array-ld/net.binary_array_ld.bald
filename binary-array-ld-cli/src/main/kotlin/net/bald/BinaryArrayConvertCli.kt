package net.bald

import net.bald.netcdf.NetCdfLdConverter
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import java.io.File
import java.io.FilterOutputStream
import java.io.OutputStream
import java.net.URI
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
        addOption("d", "download", true, "The URL from which the original file can be downloaded.")
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
        val input = opts.inputLoc?.let(::URI) ?: throw IllegalArgumentException("First argument is required: NetCDF file to convert.")
        val uri = opts.uri
        val context = opts.contextLocs.map(::URI)
        val alias = opts.aliasLocs.map(::URI)
        val downloadUrl = opts.downloadUrl

        val model = NetCdfLdConverter.getInstance().convert(input, uri, context, alias, downloadUrl)
        val outputFormat = opts.outputFormat ?: "ttl"

        modelOutput(opts.outputLoc).use { output ->
            model.write(output, outputFormat)
        }
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
