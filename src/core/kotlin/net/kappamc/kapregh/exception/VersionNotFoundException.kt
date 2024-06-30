package net.kappamc.kapregh.exception

/*
* 用于跳出游戏版本不存在的错误
*
* @author: Frish2021
* @create: 2024/6/8
*/
data class VersionNotFoundException(override val message : String) : RuntimeException(message)
