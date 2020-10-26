package net.bald.netcdf

import net.bald.BinaryArray
import net.bald.Container
import ucar.nc2.NetcdfFile
import ucar.nc2.NetcdfFiles
import java.io.Closeable
import java.io.File

/**
 * NetCDF implementation of [BinaryArray].
 * Should be closed after use.
 */
class NetCdfBinaryArray(
    override val uri: String,
    private val file: NetcdfFile
): BinaryArray, Closeable {
    override val root: Container get() = NetCdfContainer(file.rootGroup)

    override fun close() {
        file.close()
    }

    companion object {
        /**
         * Instantiate a [BinaryArray] representation of the given NetCDF file and identifying URI.
         * The resulting [NetCdfBinaryArray] should be closed after use.
         * @param fileLoc The location of the NetCDF file on the local file system.
         * @param uri The URI which identifies the dataset.
         * @return A [BinaryArray] representation of the NetCDF file.
         */
        @JvmStatic
        fun create(fileLoc: String, uri: String? = null): NetCdfBinaryArray {
            val requiredUri = uri ?: uri(fileLoc)
            val file = NetcdfFiles.open(fileLoc)
            return NetCdfBinaryArray(requiredUri, file)
        }

        private fun uri(fileLoc: String): String {
            if (File.separatorChar != '/') {
                fileLoc.replace(File.separatorChar, '/')
            }
            return File(fileLoc).toPath().toUri().toString()
        }
    }
}