package net.kappamc.kapregh.utils

/*
* 用于给String字符串添加一些操作马（bushi）
* 没什么用...
*
* @author: Frish2021
* @create: 2024/6/8
*/
enum class MessageOpcode(private val opcodeName : String) {
    ENTER("\n"),
    TAB("\t");

    override fun toString(): String = opcodeName
}
