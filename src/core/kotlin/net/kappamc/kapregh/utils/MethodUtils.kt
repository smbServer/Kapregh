package net.kappamc.kapregh.utils

object MethodUtils {
    @JvmStatic
    fun internalize(name: String): String {
        return when (name) {
            "int" -> "I"
            "float" -> "F"
            "double" -> "D"
            "long" -> "J"
            "boolean" -> "Z"
            "short" -> "S"
            "byte" -> "B"
            "void" -> "V"
            else -> name.replace('.', '/')
        }
    }

    @JvmStatic
    fun notPrimitive(name: String): Boolean {
        return when (name) {
            "int", "float", "double", "long", "boolean", "short", "byte", "void" -> false
            else -> true
        }
    }

    @JvmStatic
    fun notPrimitives(name: String): Boolean {
        return when (name) {
            "I", "F", "D", "J", "Z", "S", "B", "V" -> false
            else -> true
        }
    }
}
