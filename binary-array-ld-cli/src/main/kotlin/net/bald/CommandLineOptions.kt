package net.bald

import org.apache.commons.cli.CommandLine

class CommandLineOptions(
    private val cmd: CommandLine,
) {
    val inputLoc: String? get() {
        return cmd.args.getOrNull(0)
    }
    val outputLoc: String? get() = cmd.args.getOrNull(1)
    val uri: String? get() = cmd.getOptionValue("uri")
    val aliasLocs: List<String> get() = cmd.getOptionValue("alias")?.split(",") ?: emptyList()
    val contextLocs: List<String> get() = cmd.getOptionValue("context")?.split(",") ?: emptyList()
    val help: Boolean get() = cmd.hasOption("help")
}