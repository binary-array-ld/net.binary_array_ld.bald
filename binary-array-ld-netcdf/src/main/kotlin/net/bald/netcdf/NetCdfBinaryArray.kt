package net.bald.netcdf

import net.bald.BinaryArray
import net.bald.Distribution
import net.bald.Format
import net.bald.alias.AliasDefinition
import net.bald.context.ModelContext
import org.apache.jena.rdf.model.ResourceFactory.createResource
import org.apache.jena.shared.PrefixMapping
import ucar.nc2.AttributeContainer
import ucar.nc2.Group
import ucar.nc2.NetcdfFile
import ucar.nc2.NetcdfFiles
import java.io.File
import java.net.URI

/**
 * NetCDF implementation of [BinaryArray].
 * Should be closed after use.
 */
class NetCdfBinaryArray(
    val uri: String,
    private val file: NetcdfFile,
    private val context: ModelContext,
    val alias: AliasDefinition,
    private val downloadUrl: String?
): BinaryArray {
    override val root: NetCdfContainer get() = container(file.rootGroup)

    override val prefixMapping: PrefixMapping get() {
        return PrefixMapping.Factory.create().apply {
            setNsPrefixes(context.prefixMapping)
            internalPrefixMapping()?.let(::setNsPrefixes)
        }
    }

    override val format: Format get() = NetCdfFormat

    override val distribution: Distribution get() {
        val downloadUrl = downloadUrl ?: file.location.takeIf(::isHttp)
        val res = downloadUrl?.let(::createResource)
        return NetCdfDistribution(res)
    }

    private fun isHttp(loc: String): Boolean {
        return try {
            val scheme = URI(loc).scheme
            scheme == "http" || scheme == "https"
        } catch (e: Exception) { false }
    }

    val prefixSrc: String? get() = prefixSourceName()

    override fun close() {
        file.close()
    }

    private fun container(group: Group): NetCdfContainer {
        return NetCdfRootContainer(this, group)
    }

    private fun internalPrefixMapping(): PrefixMapping? {
        return prefixSource()?.let(::NetCdfPrefixMappingBuilder)?.build()
    }

    private fun prefixSource(): AttributeContainer? {
        return prefixSrc?.let { name ->
            file.findGroup(name)
                ?: file.findVariable(name)
                ?: throw IllegalStateException("Prefix group or variable $name not found.")
        }
    }

    private fun prefixSourceName(): String? {
        return file.findGlobalAttribute(Attribute.prefix)?.let { attr ->
            attr.stringValue
                ?: throw IllegalStateException("Global prefix attribute ${Attribute.prefix} must have a string value.")
        }
    }

    private object Attribute {
        const val prefix = "bald__isPrefixedBy"
    }

    companion object {
        /**
         * Instantiate a [BinaryArray] representation of the given NetCDF file and identifying URI.
         * The resulting [NetCdfBinaryArray] should be closed after use.
         * @param fileLoc The location of the NetCDF file on the local file system.
         * @param uri The URI which identifies the dataset.
         * @param context The external context with which to resolve prefix mappings.
         * @param alias The alias definition with which to resolve resource and property references.
         * @param downloadUrl The URL from which the file can be downloaded, if it has one. Otherwise, null.
         * @return A [BinaryArray] representation of the NetCDF file.
         */
        @JvmStatic
        fun create(
            fileLoc: String,
            uri: String? = null,
            context: ModelContext? = null,
            alias: AliasDefinition? = null,
            downloadUrl: String? = null
        ): NetCdfBinaryArray {
            val file = NetcdfFiles.open(fileLoc)
            val requiredUri = uri ?: uri(fileLoc)
            return create(file, requiredUri, context, alias, downloadUrl)
        }

        /**
         * @see create
         */
        @JvmStatic
        fun create(fileLoc: String, uri: String? = null): NetCdfBinaryArray {
            return create(fileLoc, uri, null, null)
        }

        /**
         * @see [create].
         */
        @JvmStatic
        fun create(
            file: NetcdfFile,
            uri: String,
            context: ModelContext? = null,
            alias: AliasDefinition? = null,
            downloadUrl: String? = null
        ): NetCdfBinaryArray {
            val requiredContext = context ?: ModelContext.Empty
            val requiredAlias = alias ?: AliasDefinition.Empty
            return NetCdfBinaryArray(uri, file, requiredContext, requiredAlias, downloadUrl)
        }

        private fun uri(fileLoc: String): String {
            if (File.separatorChar != '/') {
                fileLoc.replace(File.separatorChar, '/')
            }
            return File(fileLoc).toPath().toUri().toString()
        }
    }
}