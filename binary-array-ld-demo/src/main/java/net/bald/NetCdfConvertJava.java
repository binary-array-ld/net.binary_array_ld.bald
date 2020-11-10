package net.bald;

import net.bald.context.ContextBinaryArray;
import net.bald.model.ModelBinaryArrayConverter;
import net.bald.netcdf.NetCdfBinaryArray;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Demonstration of how to call the API in Java code.
 */
public class NetCdfConvertJava {
    public static void convert() throws Exception {
        BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example");
        Model model = ModelBinaryArrayConverter.convert(ba);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithExternalPrefixes() throws Exception {
        BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example");
        PrefixMapping context = ModelFactory.createDefaultModel().read("/path/to/context.json", "json-ld");
        BinaryArray contextBa = ContextBinaryArray.create(ba, context);
        Model model = ModelBinaryArrayConverter.convert(contextBa);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }
}
