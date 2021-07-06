package uk.gov.meto.netcdf.integration.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.meto.netcdf.cdl.Application;
import ucar.nc2.NetcdfFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IT_CDLConversion {

	private static final Logger LOG = LoggerFactory.getLogger(IT_CDLConversion.class);

	@Test
	public void shouldConvertToReadableNetCDF() {
		Application app = new Application();
		try {
			app.main(null);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		String filename = "array_geo.nc";
		NetcdfFile ncfile = null;
		try {
			assertNull(ncfile);
			ncfile = NetcdfFile.open(filename);
			// process( ncfile);
		} catch (IOException ioe) {
			fail("Unexpected exception: " + ioe.getMessage());
			LOG.error("trying to open " + filename, ioe);
		} finally {
			if (null != ncfile)
				try {
					ncfile.close();
					System.out.println(ncfile.toString());
					LOG.debug(ncfile.toString());
				} catch (IOException ioe) {
					fail("Unexpected exception: " + ioe.getMessage());
					LOG.error("trying to close " + filename, ioe);
				}
		}
	}
}
