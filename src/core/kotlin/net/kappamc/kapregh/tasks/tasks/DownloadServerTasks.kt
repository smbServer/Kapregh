package net.kappamc.kapregh.tasks.tasks

import net.kappamc.kapregh.tasks.Task
import net.kappamc.kapregh.utils.DownloadUtils
import net.kappamc.kapregh.utils.file.JarUtils
import net.kappamc.kapregh.utils.game.GameUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

/*
* 用于下载服务端本体
*
* @author: Frish2021
* @create: 2024/6/9
*/
open class DownloadServerTasks : Task() {
    @TaskAction
    fun downloadMojangMappingTasks() {
        logger.info("Minecraft version: $mcVersion")
        logger.info("Kapregh downloading server...")

        if (GameUtil.hasVersion(extensions)) {
            val serverFile = GameUtil.getServerFile(extensions)
            val bundlerUrl = GameUtil.getBundlerUrl(extensions)
            val bundlerFile = GameUtil.getBundlerFile(extensions)
            val bundlerSHA = GameUtil.getBundlerSHA(extensions)

            if (!bundlerFile.exists()) {
                val downloadFile = DownloadUtils.downloadFile(bundlerUrl, bundlerFile, bundlerSHA)

                if (downloadFile) {
                    val versionsList = JarUtils.readJarResourcesFileToString(bundlerFile, "META-INF/versions.list")
                    val split = versionsList.split("\t")
                    val serverPath = "META-INF/versions/${split[2]}"

                    if (serverFile.exists()) {
                        val bytes = serverFile.inputStream().readBytes()

                        if (bytes.isEmpty()) {
                            FileUtils.delete(serverFile)
                            JarUtils.getFileFromJar(bundlerFile, serverFile, serverPath)
                        }
                    } else {
                        JarUtils.getFileFromJar(bundlerFile, serverFile, serverPath)
                    }

                    logger.info("Successfully downloaded ${serverFile.absolutePath}.")
                }
            } else {
                logger.info("${serverFile.absolutePath} was download.")
            }
        }
    }
}
