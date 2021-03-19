# Binary Array Linked Data

[Kotlin](https://kotlinlang.org/) library and CLI for Binary Array Linked Data (BALD) functionality.
* [GitHub Pages](https://binary-array-ld.github.io/net.binary_array_ld.bald/)

This project consists of the following modules:
* **binary-array-ld-lib** Core library containing BALD functionality. In particular, binary array to linked data (RDF) conversion.
RDF representations are provided by [Apache Jena](https://jena.apache.org/).
* **binary-array-ld-netcdf** NetCDF implementation of binary array concepts.
* **binary-array-ld-cli** Command line interface for converting NetCDF metadata to RDF.
* **binary-array-ld-test** Common test utilities used by other modules.
* **binary-array-ld-demo** Demonstrations of BALD API usage.

## Usage

See the [GitHub pages](https://binary-array-ld.github.io/net.binary_array_ld.bald/usage.html) for usage documentation.

## Development

Note that, in order to run the automated tests for this project,
the `ncgen` command line tool must be available on your system.

You can use Maven to build this project and each of its modules with `mvn clean package`.
After building, the JAR for the command line application is located at `binary-array-ld-cli/target/bald-cli.jar`.



