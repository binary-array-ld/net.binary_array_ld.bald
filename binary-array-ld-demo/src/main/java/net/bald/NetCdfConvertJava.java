package net.bald;

import net.bald.netcdf.NetCdfLdConverter;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Demonstration of how to call the API in Java code.
 */
public class NetCdfConvertJava {
    public static void convert() throws Exception {
        File input = new File("/path/to/input.nc");
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, null);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithExternalPrefixes() throws Exception {
        File input = new File("/path/to/input.nc");
        File context = new File("/path/to/context.json");
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", Arrays.asList(context), null);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithAliases() throws Exception {
        File input = new File("/path/to/input.nc");
        File alias = new File("/path/to/alias.ttl");
        Model model = NetCdfLdConverter.INSTANCE.convert(input, "http://test.binary-array-ld.net/example", null, Arrays.asList(alias));

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }
}
