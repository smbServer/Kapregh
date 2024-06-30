package net.kappamc.kapregh.tasks

import net.kappamc.kapregh.tasks.registry.Registry
import net.kappamc.kapregh.tasks.registry.TaskRegistry
import net.kappamc.kapregh.tasks.tasks.*
import org.gradle.api.Project

/*
* 集中注册Task的
*
* @author: Frish2021
* @create: 2024/6/8
*/
class KapreghTasks(project : Project) {
    private val registry : Registry<Task> = TaskRegistry(project)

    // Used to register gradle task in this plugin.
    init {
        registry.createTasks("downloadMojangMapping", DownloadMojangMappingTasks::class.java)
        registry.createTasks("downloadBuildDataMapping", DownloadBuildDataMappingTask::class.java)
        registry.createTasks("downloadServer", DownloadServerTasks::class.java)
        registry.createTasks("cleanVersionFolder", CleanVersionFolderTask::class.java)
        registry.createTasks("antiObfuscation", AntiObfuscationTask::class.java)
        registry.createTasks("reObfuscation", ReobfuscationTask::class.java)
        registry.createTasks("generateMixinRefMap", GenerateMixinRefMapTask::class.java)
        registry.createTasks("autowriteRefmap", AutowriteRefmapTask::class.java)
    }
}
