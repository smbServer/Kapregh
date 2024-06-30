package net.kappamc.kapregh.tasks.tasks

import net.kappamc.kapregh.tasks.Task
import net.kappamc.kapregh.utils.DownloadUtils
import net.kappamc.kapregh.utils.game.GameUtil
import org.gradle.api.tasks.TaskAction


/*
* 用于下载Mojang官方的服务端的映射
*
* @author: Frish2021
* @create: 2024/6/9
*/
open class DownloadMojangMappingTasks : Task() {
    @TaskAction
    fun downloadMojangMappingTasks() {
        logger.info("Minecraft version: $mcVersion")
        logger.info("Kapregh downloading mojang server mapping...")

        if (GameUtil.hasVersion(extensions)) {
            val mappingUrl = GameUtil.getMappingUrl(extensions)
            val mappingFile = GameUtil.getMojangMappingFile(extensions)

            if (!mappingFile.exists()) {
                DownloadUtils.downloadFile(mappingUrl, mappingFile)
            } else {
                logger.info("${mappingFile.absolutePath} was download.")
            }
        }
    }
}
