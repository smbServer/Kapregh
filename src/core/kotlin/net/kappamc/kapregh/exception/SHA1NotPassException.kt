package net.kappamc.kapregh.exception

/*
* 用于跳出关于SHA-1的错误，如SHA-1校验失败
*
* @author: Frish2021
* @create: 2024/6/8
*/
data class SHA1NotPassException(override val message : String) : RuntimeException(message)
