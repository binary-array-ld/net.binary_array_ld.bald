package uk.gov.meto.netcdf.cdl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		URL url = Application.class.getClass().getResource("/array_geo.cdl");
		System.out.println("Starting the main method: " + url.getPath());
		File cdlFile = new File(url.getPath());
		System.out.println("exists: " + cdlFile.exists());
		System.out.println(cdlFile.getCanonicalPath());
		List<String> callParams = new ArrayList<String>(
		        Arrays.asList("ncgen", "-o", "array_geo.nc", url.getPath()));
		LOG.debug("Command is: {}", callParams);
		Process process;
		try {
			System.out.println(new File(url.getPath()).exists());
			ProcessBuilder processBuilder = new ProcessBuilder(callParams);
			process = processBuilder.start();
			int exitCode = process.waitFor();
			System.out.println(new File("array_geo.nc").exists());
		} catch (Exception e) {
			LOG.error("process creation failed");
			throw e;
		}

	}

}