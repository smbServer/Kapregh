pluginManagement {
    val kotlin_code_version : String by settings

    plugins {
        kotlin("jvm") version kotlin_code_version
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "Kapregh"
