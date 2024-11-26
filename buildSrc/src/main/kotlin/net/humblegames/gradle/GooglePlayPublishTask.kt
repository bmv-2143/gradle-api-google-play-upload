package net.humblegames.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GooglePlayPublishTask : DefaultTask() {

    @get:Input
    abstract val trackToPublish: Property<String>

    @get:Input
    abstract val releaseName: Property<String>

    @TaskAction
    fun act() {
        print("GooglePlayPublishTask: ACTION")
    }
}
