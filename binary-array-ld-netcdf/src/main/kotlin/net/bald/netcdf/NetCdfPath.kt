package net.bald.netcdf

/**
 * A UNIX-style path for traversing NetCDF metadata structures.
 * See http://cfconventions.org/cf-conventions/cf-conventions.html#_scope
 */
interface NetCdfPath {
    /**
     * Obtain the variable that is located by this path,
     * relative to the given group.
     * @param group The initial group against which to resolve the path.
     * @return The variable located by this path, if it exists. Otherwise, null.
     */
    fun locateVar(group: NetCdfContainer): NetCdfVar?

    class Base(
        private val head: Segment,
        private val tail: NetCdfPath
    ): NetCdfPath {
        override fun locateVar(group: NetCdfContainer): NetCdfVar? {
            return head.next(group)?.let(tail::locateVar)
        }
    }

    class Tail(
        private val name: String
    ): NetCdfPath {
        override fun locateVar(group: NetCdfContainer): NetCdfVar? {
            return group.variable(name)
        }
    }

    companion object {
        /**
         * Parse a path from a string.
         * @param path The UNIX-style path.
         * @return The path implementation.
         */
        fun parse(path: String): NetCdfPath {
            val segments = path.split('/')
            return create(segments.dropLast(1), segments.last())
        }

        /**
         * Create a path that locates a variable.
         * @param path The sequence of group relations that locate the group containing the variable.
         * @param varName The local or short name of the variable.
         * @return The path which locates the variable.
         */
        fun create(path: List<String>, varName: String): NetCdfPath {
            val tail = Tail(varName)
            return path.map(Segment::parse).foldRight(tail, ::Base)
        }
    }

    /**
     * A segment of a [NetCdfPath] that relates one group to another.
     */
    interface Segment {
        /**
         * Obtain the successor to the given group.
         * @param group The initial group.
         * @return The successor group.
         */
        fun next(group: NetCdfContainer): NetCdfContainer?

        object Root: Segment {
            override fun next(group: NetCdfContainer): NetCdfContainer? {
                return group.root
            }
        }

        object Parent: Segment {
            override fun next(group: NetCdfContainer): NetCdfContainer? {
                return group.parent
            }
        }

        object Empty: Segment {
            override fun next(group: NetCdfContainer): NetCdfContainer? {
                return group
            }
        }

        class Child(
            private val name: String
        ): Segment {
            override fun next(group: NetCdfContainer): NetCdfContainer? {
                return group.subContainer(name)
            }
        }

        companion object {
            fun parse(segment: String): Segment {
                return when (segment) {
                    "" -> Root
                    "." -> Empty
                    ".." -> Parent
                    else -> Child(segment)
                }
            }
        }
    }
}