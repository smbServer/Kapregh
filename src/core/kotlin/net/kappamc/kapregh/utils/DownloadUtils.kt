package net.kappamc.kapregh.utils

import net.kappamc.kapregh.exception.SHA1NotPassException
import net.kappamc.kapregh.utils.file.FileUtils
import org.apache.logging.log4j.Logger
import java.io.File

/*
* 主要下载网络上的文件...
*
* @author: Frish2021
* @create: 2024/6/8
*/
object DownloadUtils {
    private val logger : Logger = LogUtils.getLogger()

    @JvmStatic
    fun downloadFile(url: String, file: File, sha: String) : Boolean {
        var pass : Boolean = false

        for (iss in 0..6) {
            val bytes = FileUtils.readFile(url)

            if (!file.exists()) {
                file.createNewFile()
            }

            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bytes)

            if (FileUtils.getFileSHA1(file) != sha) {
                if (iss < 6) {
                    org.apache.commons.io.FileUtils.delete(file)
                } else {
                    org.apache.commons.io.FileUtils.delete(file)
                    throw SHA1NotPassException("SHA1 has been tested more than 5 times, and the test has not passed.")
                }
            } else {
                pass = true
                break
            }
        }

        return pass
    }

    @JvmStatic
    fun downloadFile(url: String, file: File) : Boolean {
        var pass : Boolean = false

        for (iss in 0..6) {
            val bytes = FileUtils.readFile(url)

            if (!file.exists()) {
                file.createNewFile()
            }

            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bytes)
            pass = true
        }

        return pass
    }
}
