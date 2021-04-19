# Context

The BALD CLI and library allow you to specify contextual prefix mappings to be resolved in the NetCDF metadata
as described in the [draft specification](http://docs.opengeospatial.org/DRAFTS/19-002.html#_externally_defined_prefixes).

## CLI 

You can provide the context as a set of JSON-LD files.
Use the `--context` or `-c` option to specify the locations of the context files
as a comma-delimited list.

#### Example
```
java -jar bald-cli.jar --context /path/to/context.json /path/to/netcdf.nc /path/to/graph.ttl
```

## Library

You can provide the context as an Apache Jena [prefix mapping](https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/shared/PrefixMapping.html).
Pass a prefix mapping (or list of mappings) to the `ModelContext.create` method to create a `ModelContext` instance.
You may also create your own implementation of `ModelContext`.

You can pass this instance to the `NetCdfBinaryArray.create` method to create a binary array with the given context.

Note that you can pass both a contextual prefix mapping, and an [alias definition](alias.md)
to create a `BinaryArray` with both.

#### Example

```java
PrefixMapping prefix = ModelFactory.createDefaultModel().read("/path/to/context.json", "json-ld");
ModelContext context = ModelContext.create(prefix);
BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example", context, null);
Model model = ModelBinaryArrayConverter.convert(ba);

try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
    model.write(output, "ttl");
}
```
