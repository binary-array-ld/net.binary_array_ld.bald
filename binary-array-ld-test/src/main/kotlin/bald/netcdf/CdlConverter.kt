package bald.netcdf

import java.io.File
import kotlin.test.fail

/**
 * This test utility uses CDL as a convenient, human-readable NetCDF format.
 * It requires the ncgen (https://www.unidata.ucar.edu/software/netcdf/docs/netcdf_utilities_guide.html#guide_ncgen)
 * utility to be available.
 * Test files are in src/main/resources/netcdf.
 */
object CdlConverter {
    /**
     * Convert a CDL resource into a temporary NetCDF file.
     * @param cdlLoc The location of the CDL resource.
     * @return The NetCDF file.
     */
    fun convertToNetCdf(cdlLoc: String): File {
        val cdlFile = createTempFile()
        javaClass.getResourceAsStream(cdlLoc).use { cdl ->
            cdlFile.outputStream().use(cdl::copyTo)
        }

        val netCdfFile = createTempFile()
        val netCdfLoc = netCdfFile.absolutePath
        val result = ProcessBuilder("ncgen", "-o", netCdfLoc, cdlFile.absolutePath).start().apply {
            errorStream.use { input -> input.copyTo(System.out) }
        }.waitFor()
        if (result != 0) {
            fail("ncgen process failed for resource $cdlLoc.")
        }

        return netCdfFile
    }
}