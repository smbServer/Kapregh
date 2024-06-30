package net.kappamc.kapregh.utils.file

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets

/*
* 一个文件工具包...
*
* @author: Frish2021
* @create: 2024/6/8
*/
object FileUtils {
    @JvmStatic
    fun readFile(path : File) : String {
        return FileUtils.readFileToString(path, StandardCharsets.UTF_8)
    }

    @JvmStatic
    fun readString(link: String): String {
        return String(readFile(link), StandardCharsets.UTF_8)
    }

    @JvmStatic
    fun readFile(link: String?): ByteArray {
        var bytes: ByteArray? = null
        try {
            val url: URL = URL(link)
            val urlConnection: URLConnection = url.openConnection()
            var connection: HttpURLConnection? = null
            if (urlConnection is HttpURLConnection) {
                connection = urlConnection
            }

            if (connection == null) {
                throw NullPointerException(String.format("Link: '%s' fail", link))
            }

            bytes = IOUtils.toByteArray(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bytes!!
    }

    @JvmStatic
    fun getFileSHA1(file: File): String = DigestUtils(MessageDigestAlgorithms.SHA_1).digestAsHex(file)
}
