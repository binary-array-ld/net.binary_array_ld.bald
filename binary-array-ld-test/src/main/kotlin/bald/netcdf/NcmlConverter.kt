package bald.netcdf

import ucar.nc2.NetcdfFile
import ucar.nc2.NetcdfFileWriter
import ucar.nc2.ncml.NcMLReader
import java.io.File

/**
 * This test utility uses NCML (https://www.unidata.ucar.edu/software/netcdf-java/v4.6/ncml/)
 * as a convenient, human-readable NetCDF format that can be processed by the available CDM tools.
 * Test files are in src/main/resources/netcdf.
 */
object NcmlConverter {
    /**
     * Convert a NCML resource into a temporary NetCDF 4 file.
     * In order to write NetCDF 4 successfully, the Unidata NetCDF C library must be available.
     * @param ncmlLoc The location of the NCML resource.
     * @return The NetCDF file.
     */
    fun writeToNetCdf(ncmlLoc: String): File {
        val netCdfFile = createTempFile()
        val netCdfLoc = netCdfFile.absolutePath
        javaClass.getResourceAsStream(ncmlLoc).use { ncml ->
            NcMLReader.writeNcMLToFile(ncml, netCdfLoc, NetcdfFileWriter.Version.netcdf4, null)
        }

        return netCdfFile
    }

    /**
     * Convert a NCML resource into a [NetcdfFile] in memory.
     * @param ncmlLoc The location of the NCML resource.
     * @return The NetCDF file representation.
     */
    fun readAsNetCdf(ncmlLoc: String): NetcdfFile {
        return javaClass.getResourceAsStream(ncmlLoc).use { ncml ->
            NcMLReader.readNcML(ncml, null)
        }
    }
}