package net.kappamc.kapregh.extension

import org.gradle.api.Action
import org.gradle.api.Project
import org.jetbrains.annotations.NotNull
import java.io.File

/*
* 一个Gradle插件的主要的扩展，
* 看似没什么用实际没什么用（doge），
* 用于设置插件的一些设置，such as 下载的游戏版本和映射
*
* @author: Frish2021
* @create: 2024/6/8
*/
open class KapreghExtension(private val project : Project) {
    @JvmField
    val url : Url = Url()
    @JvmField
    val mixin : Mixin = Mixin()

    @JvmField
    var mcVersion : String? = null
    @JvmField
    var bukkitCommitId : String? = null

    fun url(action : Action<Url>) {
        action.execute(url)
    }

    fun mixin(action : Action<Mixin>) {
        action.execute(mixin)
    }

    class Url {
        @JvmField
        var mcMetadataUrl : String = "https://launchermeta.mojang.com/mc/game/version_manifest.json"
        @JvmField
        var librariesUrl : String = "https://libraries.minecraft.net/"
        @JvmField
        var buildDataUrl : String = "https://hub.spigotmc.org/stash/rest/api/latest/projects/SPIGOT/repos/builddata/archive"

        fun mcMetadataUrl(url : String) {
            this.mcMetadataUrl = url
        }

        fun librariesUrl(url : String) {
            this.librariesUrl = url
        }

        fun buildDataUrl(url : String) {
            this.buildDataUrl = url
        }
    }

    class Mixin {
        @JvmField
        var referenceMap : String? = null

        fun referenceMap(map : String) {
            this.referenceMap = map
        }
    }

    fun getUserCache(): File {
        val userCache =
            File(project.gradle.gradleUserHomeDir, ("caches" + File.separator) + "kapregh")

        if (!userCache.exists()) {
            userCache.mkdirs()
        }

        return userCache
    }

    fun mcVersion(version : String) {
        this.mcVersion = version
    }

    fun bukkitCommitId(id : String) {
        this.bukkitCommitId = id
    }
}
