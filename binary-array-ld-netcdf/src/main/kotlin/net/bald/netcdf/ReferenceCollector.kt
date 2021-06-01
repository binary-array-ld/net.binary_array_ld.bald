package net.bald.netcdf

interface ReferenceCollector {
    val raw: String
    fun collect(vars: List<NetCdfVar>): ReferenceCollection

    class Unordered(
        override val raw: String
    ): ReferenceCollector {
        override fun collect(vars: List<NetCdfVar>): ReferenceCollection {
            return ReferenceCollection.Unordered(vars)
        }
    }

    class Ordered(
        override val raw: String
    ): ReferenceCollector {
        override fun collect(vars: List<NetCdfVar>): ReferenceCollection {
            return ReferenceCollection.Ordered(vars)
        }
    }
}