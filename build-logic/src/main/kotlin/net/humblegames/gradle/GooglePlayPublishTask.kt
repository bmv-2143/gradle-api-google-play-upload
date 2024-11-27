package net.humblegames.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GooglePlayPublishTask : DefaultTask() {

    @get:Input
    abstract val trackToPublish: Property<String>

    @get:Input
    abstract val releaseName: Property<String>

    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputDirectory
    abstract val aabDir: DirectoryProperty

    @TaskAction
    fun act() {
        println("GooglePlayPublishTask: ACTION")
        println(aabDir.get().asFile.listFiles().joinToString(", \n"))
    }
}
