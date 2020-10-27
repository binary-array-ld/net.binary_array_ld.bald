package net.bald;

import net.bald.model.ModelBinaryArrayConverter;
import net.bald.netcdf.NetCdfBinaryArray;
import org.apache.jena.rdf.model.Model;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Demonstration of how to call the API in Java code.
 */
public class NetCdfConvertJava {
    public static void convert(String inputLoc, String outputLoc, String format) throws Exception {
        BinaryArray ba = NetCdfBinaryArray.create(inputLoc, "http://test.binary-array-ld.net/example");
        Model model = ModelBinaryArrayConverter.convert(ba);

        try (OutputStream output = new FileOutputStream(outputLoc)) {
            model.write(output, format);
        }
    }

    public static void convert() throws Exception {
        convert("/path/to/input.nc", "/path/to/output.ttl", "ttl");
    }
}
