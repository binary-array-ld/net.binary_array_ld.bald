# Command Line Interface

You can download the latest release of the CLI [here](https://github.com/binary-array-ld/net.binary_array_ld.bald/releases/latest/download/bald-cli.jar).

You can browse older versions [here](https://github.com/binary-array-ld/net.binary_array_ld.bald/releases).

You can run the JAR using the `java -jar` command.
You can run the application with no arguments to output documentation:
```
java -jar bald-cli.jar
```

The application accepts arguments in the following format:
 ```
java -jar bald-cli.jar [options] inputFile [outputFile]
```

Where `inputFile` is the location of the NetCDF file to convert,
and `outputFile` is the location of the file in which to output the RDF graph.
If you don't specify an `outputFile`, the graph will be printed on the command line.

See the [quick reference](#quick-reference) for the full list of options.

#### Example

To read a NetCDF binary array and emit it to a file in [Turtle](https://www.w3.org/TR/turtle/) format:

```
java -jar bald-cli.jar --uri http://test.binary-array-ld.net/example /path/to/netcdf.nc /path/to/graph.ttl
```

### RDF Format

By default, the RDF graph output will be in [Turtle](https://www.w3.org/TR/turtle/) format.
You can use the `--output` or `-o` option to specify the RDF format to emit.
This option can accept any of the RDF formats supported by Apache Jena, eg. JSON-LD, RDFXML.
See [here](https://jena.apache.org/documentation/io/rdf-output.html#jena_model_write_formats) for supported formats.

### Context

The CLI supports [contexts](context.md).
You can find the documentation for this feature [here](context.md#cli).

### Aliases

The CLI supports [aliases](alias.md).
You can find the documentation for this feature [here](alias.md#cli).

### Quick Reference

You can supply command line options in long form with the `--` prefix or short form with `-`,
followed by their value.

| Option | Short form | Value | Default |
|--------|------------|-------|---------|
| --help | -h | Flag to show usage documentation (no value). ||
| --uri | -u | The URI which identifies the dataset. | Input file URI |
| --output | -o | Output format, eg. ttl, json-ld, rdfxml. | ttl |
| --context | -c | Comma-delimited list of JSON-LD context files. ||
| --alias | -a | Comma-delimited list of RDF alias files. ||