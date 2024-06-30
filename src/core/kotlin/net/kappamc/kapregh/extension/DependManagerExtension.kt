package net.kappamc.kapregh.extension

import org.dom4j.io.SAXReader
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File
import java.net.URI

/*
* 这个扩展只是为了方便我从Bukkit的源码文件中获取
* 依赖，Bukkit的项目管理文件是pom.xml，每次移植
* 代码的时候都要手动一遍一遍地复制粘贴，累亖劳资了 ）：
*
* @create: 2024/6/10
* @author: Frish2021
*/
open class DependManagerExtension(project : Project) {
    private val repositories = RepositoriesConfiguration(project)
    private val dependencies = DependenciesConfiguration(project)

    /*
    * Repositories code Cube
    */
    fun repositories(action: Action<RepositoriesConfiguration>) {
        action.execute(repositories)
    }

    /*
    * Dependencies code Cube
    */
    fun dependencies(action: Action<DependenciesConfiguration>) {
        action.execute(dependencies)
    }

    class RepositoriesConfiguration(val project : Project) {
        fun pomFileRepository(pomFile: File) {
            val reader = SAXReader()
            val document = reader.read(pomFile)
            val rootElement = document.rootElement

            val elements = rootElement.elements("repositories")
            if (elements != null) {
                for (element in elements) {
                    element.elements().forEach {
                        run {
                            val name = it.element("name").data
                            val url = it.element("url").data

                            project.repositories.maven {
                                it.name = name as String
                                it.url = URI.create(url as String)
                            }
                        }
                    }
                }
            }
        }
    }

    class DependenciesConfiguration(val project : Project) {
        fun pomFileDependencies(pomFile: File) {
            val reader = SAXReader()
            val document = reader.read(pomFile)
            val rootElement = document.rootElement

            val elements = rootElement.elements("dependencies")
            if (elements != null) {
                for (element in elements) {
                    element.elements().forEach {
                        run {
                            val name = it.element("groupId").data
                            val version = it.element("version").data
                            val artifactId = it.element("artifactId").data

                            project.dependencies.add("implementation", "$name:$artifactId:$version")
                        }
                    }
                }
            }
        }
    }
}
