package net.kappamc.kapregh

import net.kappamc.kapregh.api.PluginBase
import net.kappamc.kapregh.exception.ExtensionException
import net.kappamc.kapregh.extension.DependManagerExtension
import net.kappamc.kapregh.extension.KapreghExtension
import net.kappamc.kapregh.tasks.KapreghTasks
import net.kappamc.kapregh.utils.LogUtils
import net.kappamc.kapregh.utils.MessageOpcode
import net.kappamc.kapregh.utils.game.GameUtil
import org.apache.logging.log4j.Logger
import org.dom4j.io.SAXReader
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.io.File
import java.net.URI

/*
* 一个Gradle插件的屑主类，
* 主要没什么用（doge），其实是管理整个插件
*
* @author: Frish2021
* @create: 2024/6/8
*/
open class Kapregh : PluginBase {
    private val logger: Logger = LogUtils.getLogger()
    private val supportedVersion : Array<String> = arrayOf("8.4", "8.5", "8.7", "8.8")

    // register mgr
    private lateinit var tasks: KapreghTasks
    private lateinit var extension: KapreghExtension
    private lateinit var dependManager: DependManagerExtension

    override fun apply(target: Project) {
        logger.info("=================== Kapregh ===================")
        logger.info("=author: Frish2021                            =")
        logger.info("=create: 2024/6/8                             =")
        logger.info("=licence: GNU General Public License Ver. 3   =")
        logger.info("=website: www.kappamc.net                     =")
        logger.info("===============================================" + MessageOpcode.ENTER)

        val gradleVersion = target.gradle.gradleVersion
        if (!supportedVersion.contains(gradleVersion)) {
            throw RuntimeException("The Gradle ${gradleVersion} version is not supported.")
        }

        extension = target.extensions.create("kapregh", KapreghExtension::class.java, target)
        dependManager = target.extensions.create("dependManager", DependManagerExtension::class.java, target)

        target.afterEvaluate { project: Project ->
            // This is mainly running method of this plugin.
            run {
                if (extension.mcVersion == null && extension.bukkitCommitId == null) {
                    throw ExtensionException("The extension some information is null!")
                }

                logger.info("Minecraft version: ${extension.mcVersion}")
                logger.info("Bukkit commit id: ${extension.bukkitCommitId}")

                tasks = KapreghTasks(project)
                project.tasks.getByName("compileJava").finalizedBy(
                    project.tasks.getByName("generateMixinRefMap"),
                    project.tasks.getByName("reObfuscation")
                )

                project.repositories.minecraft(extension)
                project.repositories.spongepowered()
                project.repositories.tencentCloud()
                project.repositories.mavenCentral()
                project.repositories.mavenLocal()
                project.repositories.google()
                project.repositories.aliyun()
                project.repositories.fabric()

                val serverFile = GameUtil.getServerFile(extension)
                if (serverFile.exists()) {
                    project.dependencies.add("runtimeOnly", project.files(serverFile))
                }

                val deobfFile = GameUtil.getDeobfServerFile(extension)
                if (deobfFile.exists()) {
                    project.dependencies.add("compileOnly", project.files(deobfFile))
                }

                val libraries = GameUtil.getServerLibraries(extension)
                libraries.forEach { library ->
                    run {
                        project.dependencies.add("implementation", library)
                    }
                }
            }
        }
    }

    private fun RepositoryHandler.tencentCloud() {
        maven {
            it.url = URI.create("https://mirrors.tencent.com/nexus/repository/maven-public/")
            it.name = "tencentCloud"
        }
    }

    private fun RepositoryHandler.aliyun() {
        maven {
            it.url = URI.create("https://maven.aliyun.com/repository/public/")
            it.name = "aliyun"
        }
    }

    private fun RepositoryHandler.fabric() {
        maven {
            it.url = URI.create("https://mirrors.fabricmc.net/")
            it.name = "fabricmc"
        }
    }

    private fun RepositoryHandler.spongepowered() {
        maven {
            it.url = URI.create("https://repo.spongepowered.org/maven")
            it.name = "spongepowered"
        }
    }

    private fun RepositoryHandler.minecraft(extension: KapreghExtension) {
        maven {
            it.url = URI.create(extension.url.librariesUrl)
            it.name = "minecraft"
        }
    }
}
