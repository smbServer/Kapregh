package net.kappamc.kapregh.tasks.tasks

import net.kappamc.kapregh.tasks.Task
import net.kappamc.kapregh.utils.game.GameUtil
import org.gradle.api.tasks.TaskAction

open class CleanVersionFolderTask : Task() {
    @TaskAction
    fun cleanVersionDirTask() {
        logger.info("Cleaning version folder...")

        val versionDir = GameUtil.getVersionCacheDirs(extensions)

        if (versionDir.exists()) {
            project.delete(versionDir)

            logger.info("Version folder is cleaned.")
        } else {
            logger.error("Folder is not exists.")
        }
    }
}