package net.kappamc.kapregh.tasks.tasks

import net.kappamc.kapregh.tasks.Task
import net.kappamc.kapregh.utils.file.FileUtils
import net.kappamc.kapregh.utils.file.JarUtils
import net.kappamc.kapregh.utils.game.GameUtil
import org.gradle.api.tasks.TaskAction
import org.gradle.util.internal.JarUtil

/*
* 用于下载Spigot的BuildData构造映射
*
* @author: Frish2021

* */
open class DownloadBuildDataMappingTask : Task() {
    @TaskAction
    fun downloadBuildDataMappingTask() {
        logger.info("Minecraft version: $mcVersion")
        logger.info("Kapregh downloading mojang server mapping...")

        if (GameUtil.hasVersion(extensions)) {
            var mappingUrl = extensions.url.buildDataUrl
            val mappingFile = GameUtil.getBuildDataMappingJar(extensions)
            val csrgFile = GameUtil.getBuildDataMappingFile(extensions)
            val excludeFile = GameUtil.getBuildDataExcludeFile(extensions)
            val infoFile = GameUtil.getBuildDataInfoFile(extensions)

            if (extensions.bukkitCommitId != null) {
                mappingUrl = "$mappingUrl?at=${extensions.bukkitCommitId}&format=zip"
            }

            if (!mappingFile.exists()) {
                val bytes = FileUtils.readFile(mappingUrl)

                if (!mappingFile.exists()) {
                    mappingFile.createNewFile()
                }

                org.apache.commons.io.FileUtils.writeByteArrayToFile(mappingFile, bytes)
                JarUtils.getFileFromJar(mappingFile, csrgFile, "mappings/bukkit-${extensions.mcVersion}-cl.csrg")
                JarUtils.getFileFromJar(mappingFile, excludeFile, "mappings/bukkit-${extensions.mcVersion}.exclude")
                JarUtils.getFileFromJar(mappingFile, infoFile, "info.json")

                logger.info("Successfully downloaded ${mappingFile.absolutePath}.")
            } else {
                logger.info("${mappingFile.absolutePath} was download.")
            }
        }
    }
}
