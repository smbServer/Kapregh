package net.kappamc.kapregh.utils.file

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile

/*
* 从其他Gradle插件迁移过来的，
* 但是作者还是我（doge），
* 其中用处是用于Jar的文件读取或者控制
*
* @author: Frish2021
* @create: 2024/6/1
*/
object JarUtils {
    @JvmStatic
    @Suppress("NAME_SHADOWING")
    fun getFileFromJar(jarFile: File, outFile: File, fileName: String) {
        val jarFile = JarFile(jarFile)
        val entry: Enumeration<JarEntry> = jarFile.entries()
        while (entry.hasMoreElements()) {
            val nextElement = entry.nextElement()

            val name = nextElement.name
            if (name == fileName) {
                val jarEntry = jarFile.getEntry(name)
                val bytes = jarFile.getInputStream(jarEntry).readBytes()

                if (!outFile.exists()) {
                    outFile.createNewFile()

                    FileUtils.writeByteArrayToFile(outFile, bytes)
                } else {
                    if (outFile.inputStream().readBytes().isEmpty()) {
                        FileUtils.writeByteArrayToFile(outFile, bytes)
                    }
                }
            }
        }
    }

    @JvmStatic
    @Suppress("NAME_SHADOWING")
    fun readJarResourcesFile(jarFile: File, fileName: String): InputStream {
        val jarFile = JarFile(jarFile)
        var inputStream: InputStream? = null

        val entry: Enumeration<JarEntry> = jarFile.entries()
        while (entry.hasMoreElements()) {
            val nextElement = entry.nextElement()

            val name = nextElement.name
            if (name == fileName) {
                val jarEntry = jarFile.getEntry(name)

                inputStream = jarFile.getInputStream(jarEntry)
            }
        }

        return inputStream!!
    }

    @JvmStatic
    fun readJarResourcesFileToString(jarFile: File, fileName: String): String {
        val inputStream: InputStream = readJarResourcesFile(jarFile, fileName)

        return String(inputStream.readBytes(), StandardCharsets.UTF_8)
    }
}
