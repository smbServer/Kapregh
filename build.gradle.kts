plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

val targetDir = File(project.rootDir, "target")
val srcDir = File(project.rootDir, "src")
val pluginName = rootProject.name.lowercase()

group = get("project_code_group")
version = get("project_code_version")

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")

    implementation("commons-io:commons-io:2.10.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.ow2.asm:asm:${get("asm_version")}")
    implementation("org.ow2.asm:asm-analysis:${get("asm_version")}")
    implementation("org.ow2.asm:asm-commons:${get("asm_version")}")
    implementation("org.ow2.asm:asm-tree:${get("asm_version")}")
    implementation("org.ow2.asm:asm-util:${get("asm_version")}")
    implementation("org.dom4j:dom4j:2.1.4")
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        val groupName = rootProject.group as String
        create(groupName) {
            id = groupName
            implementationClass = "${groupName}.Kapregh"
        }
    }
}

publishing {
    repositories {
        maven(targetDir)
    }

    publications {
        create<MavenPublication>("mavenTool") {
            group = rootProject.group
            version = (rootProject.version) as String
            artifactId = pluginName

            from(components["java"])
        }
    }
}

tasks.publish {
    group = pluginName

    logger.lifecycle("> Building kapregh tools...")
    logger.lifecycle("> =================== Kapregh ===================")
    logger.lifecycle("> =author: Frish2021                            =")
    logger.lifecycle("> =create: 2024/6/8                             =")
    logger.lifecycle("> =licence: GNU General Public License Ver. 3   =")
    logger.lifecycle("> =website: www.kappamc.net                     =")
    logger.lifecycle("> ===============================================")
}

tasks.register<DefaultTask>("cleanTask") {
    group = pluginName

    doFirst {
        project.logger.lifecycle("Cleaning project build folder...")

        val buildDir = file(rootProject.layout.buildDirectory).absoluteFile
        if (buildDir.exists()) {
            if (targetDir.exists()) {
                delete(buildDir, targetDir)

                project.logger.lifecycle("Cleaned project build folder!")
            } else {
                fileNotFound(targetDir)
            }
        } else {
            fileNotFound(buildDir)
        }
    }
}

sourceSets {
    main {
        java.srcDirs(File(srcDir, "core/java"))
        kotlin.srcDirs(File(srcDir, "core/kotlin"))
        resources.srcDirs(File(srcDir, "core/resources"))
    }
}

fun get(key: String): String {
    return project.properties[key] as String
}

fun fileNotFound(file: File) {
    project.logger.error("Folder ${file.absolutePath} is existsn`t!")
}
