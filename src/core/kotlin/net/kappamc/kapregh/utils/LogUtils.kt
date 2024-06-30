package net.kappamc.kapregh.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/*
* 用来快捷使用日志系统而已
*
* @author: Frish2021
* @create: 2024/6/8
*/
object LogUtils {
    private val logger : Logger = LogManager.getLogger("Kapregh")

    @JvmStatic
    fun getLogger() : Logger = logger
}
