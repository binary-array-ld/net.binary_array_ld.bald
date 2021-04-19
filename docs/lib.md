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

Use the `NetCdfBinaryArray.create` method to create a new binary array representation from a NetCDF file.
NetCDF and CDL file formats are supported.
You can also optionally supply a URI as the identifier of the dataset.

You can pass the resulting `BinaryArray` instance to the `ModelBinaryArrayConverter.convert`
method to obtain an RDF graph in Apache Jena [model](https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/rdf/model/Model.html) form.
See the [Jena docs](https://jena.apache.org/tutorials/rdf_api.html) for how to use the `Model` class.

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
