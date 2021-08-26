# Aliases

The BALD CLI and library allow you to specify property and resource aliases to be resolved in the NetCDF metadata,
as described in the [draft specification](http://docs.opengeospatial.org/DRAFTS/19-002.html#_alias_definition).

## CLI

You can provide aliases as a set of RDF files.
Use the `--alias` or `-a` option to specify the locations of alias files as a comma-delimited list.
These can be the paths of files on the local file system, or the URLs of remote resources. 

Note that the alias paths must have a suitable extension in order to be parsed correctly.

#### Example
```
java -jar bald-cli.jar --alias /path/to/alias.ttl /path/to/netcdf.nc /path/to/graph.ttl
```

### Note

If your alias files are remote resources and you are using a proxy server,
you must provide the proxy host and port with the `-Dhttp.proxyHost` and `-Dhttp.proxyPort`
JVM arguments.
See [here](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html) for official documentation.

## Library

For simple usage, use the `NetCdfLd.convert` method as described [here](lib.md#simple-usage).

Alternatively, you can provide aliases as an Apache Jena [model](https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/rdf/model/Model.html).
You can create a `Model` instance programmatically, or by reading in an RDF file.
See the [Jena docs](https://jena.apache.org/tutorials/rdf_api.html) for more information.

Pass a model to the `ModelAliasDefinition.create` method to create an `AliasDefinition` instance.
You may also create your own implementation of `AliasDefinition`.

You can pass this instance to the `NetCdfBinaryArray.create` method to create a binary array with the given aliases.

Note that you can pass both a contextual [prefix mapping](context.md),
and an alias definition to create a `BinaryArray` with both.

```java
Model aliasModel = ModelFactory.createDefaultModel().read("/path/to/alias.ttl", "ttl");
AliasDefinition alias = ModelAliasDefinition.create(aliasModel);
BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example", null, alias, null);
Model model = ModelBinaryArrayConverter.convert(ba);

try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
    model.write(output, "ttl");
}
```
