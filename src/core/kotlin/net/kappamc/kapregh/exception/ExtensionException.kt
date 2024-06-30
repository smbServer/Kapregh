package net.kappamc.kapregh.exception

/*
* 用于跳出关于扩展的错误，如扩展内容缺失什么的...
*
* @author: Frish2021
* @create: 2024/6/8
*/
data class ExtensionException(override val message : String) : RuntimeException(message)
