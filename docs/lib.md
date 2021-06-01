# Library

Javadocs are available [here](todo).
You can find Java examples [here](https://github.com/binary-array-ld/net.binary_array_ld.bald/tree/master/binary-array-ld-demo/src/main/java/net/bald).

To use the BALD core library in your Maven project, add the following dependency:

```xml
<dependency>
    <groupId>net.binary-array-ld</groupId>
    <artifactId>binary-array-ld-lib</artifactId>
    <version>${bald.version}</version>
</dependency>
```

To convert NetCDF metadata files, you can use the pre-made NetCDF implementation in the `binary-array-ld-netcdf` module.
To use this module, add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>net.binary-array-ld</groupId>
    <artifactId>binary-array-ld-netcdf</artifactId>
    <version>${bald.version}</version>
</dependency>
```

### Simple Usage

You can use the `NetCdfLd.convert` method to convert NetCDF binary array metadata to an RDF graph in Apache Jena [model](https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/rdf/model/Model.html) form.
See the [Jena docs](https://jena.apache.org/tutorials/rdf_api.html) for how to use the `Model` class.
The model can be serialised to a file using the `write` method.

The `NetCdfLd.convert` method accepts the following parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| input     | File | The NetCDF binary array file to convert. |
| uri       | String | The URI of the binary array. Optional. |
| contexts  | List\<File> | The files containing [JSON-LD contexts](#context). Optional. |
| aliases   | List\<File> | The files containing [alias definitions](#aliases). Optional. |

Optional parameters are optional in Kotlin or nullable in Java. 

#### Example
To read a NetCDF binary array and emit it to a file in [Turtle](https://www.w3.org/TR/turtle/) format:

Kotlin
```kotlin
val input = File("/path/to/input.nc")
val model = NetCdfLd.convert(input, "http://test.binary-array-ld.net/example")
File("/path/to/output.ttl").outputStream().use { output ->
    model.write(output, "ttl")
}
```
Java
```java
File input = new File("/path/to/input.nc");
Model model = NetCdfLd.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, null);

try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
    model.write(output, "ttl");
}
```

### Advanced Usage

For some purposes, you may prefer to use the components of the BALD library individually.

You can use the `NetCdfBinaryArray.create` method to create a new binary array representation from a NetCDF file.
NetCDF and CDL file formats are supported.
You can also optionally supply a URI as the identifier of the dataset.

You can pass the resulting `BinaryArray` instance to the `ModelBinaryArrayConverter.convert`
method to obtain the RDF graph as a Jena model.

You can also implement the `BinaryArray` interface with your own binary array metadata representations.

#### Example
To read a NetCDF binary array and emit it to a file in [Turtle](https://www.w3.org/TR/turtle/) format:

Kotlin
```kotlin
val ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example")
val model = ModelBinaryArrayConverter.convert(ba)
File("/path/to/output.ttl").outputStream().use { output ->
    model.write(output, "ttl")
}
```
Java
```java
BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.ttl", "http://test.binary-array-ld.net/example");
Model model = ModelBinaryArrayConverter.convert(ba);

try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
    model.write(output, "ttl");
}
```

### Context

The library supports [contexts](context.md).
You can find the documentation for this feature [here](context.md#library).

### Aliases

The library supports [aliases](alias.md).
You can find the documentation for this feature [here](alias.md#library).
