package net.bald;

import net.bald.alias.AliasDefinition;
import net.bald.context.ModelContext;
import net.bald.alias.ModelAliasDefinition;
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
        PrefixMapping prefix = ModelFactory.createDefaultModel().read("/path/to/context.json", "json-ld");
        ModelContext context = ModelContext.create(prefix);
        BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example", context, null);
        Model model = ModelBinaryArrayConverter.convert(ba);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }

    public static void convertWithAliases() throws Exception {
        Model aliasModel = ModelFactory.createDefaultModel().read("/path/to/alias.ttl", "ttl");
        AliasDefinition alias = ModelAliasDefinition.create(aliasModel);
        BinaryArray ba = NetCdfBinaryArray.create("/path/to/input.nc", "http://test.binary-array-ld.net/example", null, alias);
        Model model = ModelBinaryArrayConverter.convert(ba);

        try (OutputStream output = new FileOutputStream("/path/to/output.ttl")) {
            model.write(output, "ttl");
        }
    }
}
