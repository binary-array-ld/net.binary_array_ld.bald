package net.bald;

import net.bald.netcdf.NetCdfLdConverter;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

/**
 * Demonstration of how to call the API in Java code.
 */
public class NetCdfConvertJava {
    public static void convert() throws Exception {
        URI input = new File("/path/to/input.nc").toURI();
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, null, null);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithExternalPrefixes() throws Exception {
        URI input = new File("/path/to/input.nc").toURI();
        URI context = new File("/path/to/context.json").toURI();
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", Arrays.asList(context), null, null);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithAliases() throws Exception {
        URI input = new File("/path/to/input.nc").toURI();
        URI alias = new File("/path/to/alias.ttl").toURI();
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, Arrays.asList(alias), null);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithDownloadUrl() throws Exception {
        String downloadUrl = "http://test.binary-array-ld.net/download/netcdf.nc";
        URI input = new File("/path/to/input.nc").toURI();
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, null, downloadUrl);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }
}
