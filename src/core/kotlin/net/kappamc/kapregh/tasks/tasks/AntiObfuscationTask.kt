package net.kappamc.kapregh.tasks.tasks

import net.kappamc.kapregh.tasks.Task
import net.kappamc.kapregh.utils.game.GameUtil
import net.kappamc.kapregh.utils.mappings.MappingUtils
import net.kappamc.kapregh.utils.mappings.RemappingUtils
import org.gradle.api.tasks.TaskAction

open class AntiObfuscationTask : Task() {
    @TaskAction
    fun antiObfuscationTask() {
        logger.info("Minecraft version: $mcVersion")
        logger.info("Kapregh anti-obfuscating mojang server mapping...")

        val serverFile = GameUtil.getServerFile(extensions)
        val serverDeobfFile = GameUtil.getDeobfServerFile(extensions)

         if (!serverDeobfFile.exists()) {
             if (serverFile.exists()) {
                 val mappingUtils = MappingUtils.getInstance(extensions)
                 val remappingUtils = RemappingUtils.getInstance("antiObfuscation", mappingUtils.getMap(true))
                 remappingUtils.analyzeJar(serverFile)
                 remappingUtils.remappingJar(serverFile, serverDeobfFile)

                 logger.info("Server anti-obfuscation is complete.")
             } else {
                 logger.error("The server file does not exist, please execute the 'AntiObfuscationTask' task and then execute this task.")
             }
         } else {
             logger.info("The anti-obfuscation operation is complete, please do not do it again.")
         }
    }
}
