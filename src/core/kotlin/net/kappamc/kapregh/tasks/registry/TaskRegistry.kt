package net.kappamc.kapregh.tasks.registry

import net.kappamc.kapregh.tasks.Task
import org.gradle.api.Project

/*
* Regisry的实现接口...
*
* @author: Frish2021
* @create: 2024/6/8
*/
class TaskRegistry<T : org.gradle.api.Task>(private val project : Project) : Registry<T> {
    override fun createTasks(name: String, clazz: Class<out T>) {
        project.tasks.create(name, clazz)
    }
}
