package net.bald.netcdf

class ReferenceValueParser(
    private val group: NetCdfContainer
) {
    fun parse(value: String): ReferenceCollection? {
        return value.trim().let(::doParse)
    }

    private fun doParse(value: String): ReferenceCollection? {
        val collector = if (value.startsWith('(') && value.endsWith(')')) {
            value.substringAfter('(').substringBeforeLast(')').let(ReferenceCollector::Ordered)
        } else {
            ReferenceCollector.Unordered(value)
        }

        val nodes = split(collector.raw).map(::resolve).toList()
        val referenceNodes = nodes.filterNotNull()

        return if (referenceNodes.size != nodes.size) null else {
            collector.collect(referenceNodes)
        }
    }

    private fun split(values: String): Sequence<String> {
        return values.splitToSequence(' ')
            .filterNot(String::isEmpty)
            .map(String::trim)
    }

    private fun resolve(value: String): NetCdfVar? {
        return NetCdfPath.parse(value).locateVar(group)
    }
}