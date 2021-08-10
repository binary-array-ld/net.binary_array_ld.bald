# Download URL

The BALD CLI and library allow you to specify a download URL for the given NetCDF file,
as described in the [draft specification](http://docs.opengeospatial.org/DRAFTS/19-002.html#_download_url).

If you don't specify a download URL, but the given location of the NetCDF file is a remote URL (ie. it has `http` or `https` scheme),
then the download URL will be inferred to be the same.

## CLI

You can optionally provide the download URL as a command line argument.
Use the `--download` or `-d` option to specify the download URL for the NetCDF file.

#### Example
```
java -jar bald-cli.jar --download http://test.binary-array-ld.net/download/netcdf.nc /path/to/netcdf.nc /path/to/graph.ttl
```

## Library

You can optionally provide the download URL as a parameter to the `NetCdfBinaryArray.create` method.
Otherwise, you can simply pass in a null value.

#### Example
```java
String downloadUrl = "http://test.binary-array-ld.net/download/netcdf.nc";
URI input = new File("/path/to/input.nc").toURI();
Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, null, downloadUrl);

try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
    model.write(output, "ttl");
}
```