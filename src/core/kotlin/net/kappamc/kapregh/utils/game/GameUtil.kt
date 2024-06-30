package net.kappamc.kapregh.utils.game

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.kappamc.kapregh.extension.KapreghExtension
import net.kappamc.kapregh.exception.VersionNotFoundException
import net.kappamc.kapregh.utils.file.FileUtils
import net.kappamc.kapregh.utils.file.JarUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.nio.charset.StandardCharsets

/*
* 管理与游戏相关的代码...
*
* @author: Frish2021
* @create: 2024/6/8
*/
object GameUtil {
    @JvmStatic
    fun getVersionCacheDir(extension: KapreghExtension): File {
        val file = File(extension.getUserCache(), extension.mcVersion!!)

        if (!file.exists()) {
            file.mkdirs()
        }

        return file
    }

    @JvmStatic
    fun getVersionCacheDirs(extension: KapreghExtension): File = File(extension.getUserCache(), extension.mcVersion!!)

    @JvmStatic
    private fun getVersionMappingDir(extension: KapreghExtension): File {
        val file = File(getVersionCacheDir(extension), "mappings-v${extension.mcVersion!!}")

        if (!file.exists()) {
            file.mkdirs()
        }

        return file
    }

    @JvmStatic
    private fun getMetaDataStr(extension: KapreghExtension): String = FileUtils.readString(extension.url.mcMetadataUrl)

    @JvmStatic
    fun hasVersion(extension: KapreghExtension): Boolean {
        val versionList = ArrayList<String>()
        val json = Gson().fromJson(getMetaDataStr(extension), JsonObject::class.java)["versions"].asJsonArray

        for (jsonElement in json) {
            val id = jsonElement.asJsonObject["id"].asString
            versionList.add(id)
        }

        if (versionList.contains(extension.mcVersion)) {
            return true
        } else {
            throw VersionNotFoundException("Minecraft version ${extension.mcVersion} is not found.")
        }
    }

    @JvmStatic
    private fun getVersionUrl(extension: KapreghExtension): String {
        val json = Gson().fromJson(getMetaDataStr(extension), JsonObject::class.java)["versions"].asJsonArray

        var url = ""
        for (jsonElement in json) {
            val id = jsonElement.asJsonObject["id"].asString
            if (id.equals(extension.mcVersion)) {
                url = jsonElement.asJsonObject["url"].asString
            }
        }

        return url
    }

    @JvmStatic
    fun getServerLibraries(extension: KapreghExtension) : MutableList<String> {
        val list : MutableList<String> = ArrayList()
        val bundlerFile = getBundlerFile(extension)

        if (bundlerFile.exists()) {
            val librariesStr = JarUtils.readJarResourcesFile(bundlerFile, "META-INF/libraries.list")
            val enter = IOUtils.readLines(librariesStr, StandardCharsets.UTF_8)

            for (s in enter) {
                val split1 = s.split("\t")
                val librariesName = split1[1]

                list.add(librariesName)
            }
        }

        return list
    }

    @JvmStatic
    fun getVersionJson(extension: KapreghExtension): String = FileUtils.readString(getVersionUrl(extension))

    @JvmStatic
    fun getBundlerUrl(extension: KapreghExtension): String {
        val json = Gson().fromJson(getVersionJson(extension), JsonObject::class.java)["downloads"].asJsonObject

        return json["server"].asJsonObject["url"].asString
    }

    @JvmStatic
    fun getBundlerSHA(extension: KapreghExtension): String {
        val json = Gson().fromJson(getVersionJson(extension), JsonObject::class.java)["downloads"].asJsonObject

        return json["server"].asJsonObject["sha1"].asString
    }

    @JvmStatic
    fun getMappingUrl(extension: KapreghExtension): String {
        val readFileToString =
            org.apache.commons.io.FileUtils.readFileToString(getBuildDataInfoFile(extension), "utf-8")
        val json = Gson().fromJson(readFileToString, JsonObject::class.java)["mappingsUrl"].asString

        return json
    }

    @JvmStatic
    fun getMojangMappingFile(extension: KapreghExtension): File =
        File(getVersionMappingDir(extension), "mapping-mojang-v${extension.mcVersion!!}.txt")

    @JvmStatic
    fun getBuildDataMappingJar(extension: KapreghExtension): File =
        File(getVersionMappingDir(extension), "mapping-buildData-v${extension.mcVersion!!}.jar")

    @JvmStatic
    fun getBuildDataMappingFile(extension: KapreghExtension): File =
        File(getVersionMappingDir(extension), "mapping-buildData-v${extension.mcVersion!!}.csrg")

    @JvmStatic
    fun getBuildDataExcludeFile(extension: KapreghExtension): File =
        File(getVersionMappingDir(extension), "exclude-buildData-v${extension.mcVersion!!}.exclude")

    @JvmStatic
    fun getBuildDataInfoFile(extension: KapreghExtension): File =
        File(getVersionMappingDir(extension), "info-buildData-v${extension.mcVersion!!}.json")

    @JvmStatic
    fun getBundlerFile(extension: KapreghExtension): File =
        File(getVersionCacheDir(extension), "bundler-v${extension.mcVersion!!}.jar")

    @JvmStatic
    fun getServerFile(extension: KapreghExtension): File =
        File(getVersionCacheDir(extension), "server-v${extension.mcVersion!!}.jar")

    @JvmStatic
    fun getDeobfServerFile(extension: KapreghExtension): File =
        File(getVersionCacheDir(extension), "server-developer-v${extension.mcVersion!!}.jar")
}
