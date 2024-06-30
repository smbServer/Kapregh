pluginManagement {
    val kotlin_code_version : String by settings

    plugins {
        kotlin("jvm") version kotlin_code_version
    }
}

rootProject.name = "Kapregh"
