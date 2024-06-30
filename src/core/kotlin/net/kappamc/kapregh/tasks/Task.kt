package net.kappamc.kapregh.tasks

import net.kappamc.kapregh.extension.KapreghExtension
import net.kappamc.kapregh.utils.LogUtils
import org.apache.logging.log4j.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import java.io.File

/*
* 所有Task它爹（Task的父类，要注册Task要想继承它）
*
* @author: Frish2021
* @create: 2024/6/8
*/
abstract class Task : DefaultTask() {
    @JvmField
    val logger : Logger = LogUtils.getLogger()
    @Input
    @JvmField
    val extensions : KapreghExtension
    @JvmField
    @Input
    val classesDir : File
    @JvmField
    @Input
    val resourcesDir : File
    @JvmField
    val mcVersion : String

    init {
        group = "kapregh"

        extensions = project.extensions.getByType(KapreghExtension::class.java)
        classesDir = project.layout.buildDirectory.get().dir("classes").asFile
        resourcesDir = project.layout.buildDirectory.get().dir("resources").asFile
        mcVersion = extensions.mcVersion!!
    }
}
