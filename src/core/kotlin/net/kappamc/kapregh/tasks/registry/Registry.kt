package net.kappamc.kapregh.tasks.registry

import org.gradle.api.Task

/*
* 一个接口...
*
* @author: Frish2021
* @create: 2024/6/8
*/
interface Registry<T : Task> {
    fun createTasks(name : String, clazz : Class<out T>)
}
