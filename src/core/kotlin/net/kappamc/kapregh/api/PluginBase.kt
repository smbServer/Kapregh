package net.kappamc.kapregh.api

import net.kappamc.kapregh.api.annotations.PluginBase
import org.gradle.api.Plugin
import org.gradle.api.Project

/*
* 插件主类它爹
*
* @author: Frish2021
* @create: 2024/6/8
*/
@PluginBase
interface PluginBase : Plugin<Project>
