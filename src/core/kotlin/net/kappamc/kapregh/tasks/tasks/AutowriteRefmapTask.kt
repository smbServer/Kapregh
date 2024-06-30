package net.kappamc.kapregh.tasks.tasks

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.kappamc.kapregh.tasks.Task
import org.apache.commons.io.FileUtils
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

open class AutowriteRefmapTask : Task() {
    @TaskAction
    fun autowriteRefmapTask() {
        logger.info("authwriting refmap...")

        val java = project.extensions.getByType(JavaPluginExtension::class.java)
        for (sourceSet in java.sourceSets) {
            if (extensions.mixin.mixinConfig != null) {
                if (!resourcesDir.exists()) {
                    resourcesDir.mkdir()
                }
                val dir = File(resourcesDir, sourceSet.name)
                if (!dir.exists()) {
                    dir.mkdir()
                }

                val files = File(dir, extensions.mixin.mixinConfig!!)
                if (files.exists()) {
                    val json : JsonObject = Gson().fromJson(FileUtils.readFileToString(files, "utf-8"), JsonObject::class.java)
                    json.addProperty("refmap", extensions.mixin.referenceMap!!)

                    try {
                        FileUtils.write(
                            files,
                            GsonBuilder().setPrettyPrinting().create().toJson(json),
                            StandardCharsets.UTF_8
                        )

                        logger.info("authwrited refmap.")
                    } catch (e: IOException) {
                        logger.error(e.message)
                    }
                }
            } else {
                logger.error("mixinConfig is null!!")
            }
        }
    }
}
