package net.humblegames.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class GooglePlayPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val googlePlayPublishExtension =
            project.extensions.create(
                "googlePlayPublishExtension",
                GooglePlayPublishExtension::class.java,
            )

        project.tasks.register("MyGooglePlayPublishTask", GooglePlayPublishTask::class.java)
            .configure {
                group = "GooglePlayGroup"
                description = "This is Google Play Publish Task"
                trackToPublish.set(googlePlayPublishExtension.trackToPublish)
                releaseName.set(googlePlayPublishExtension.releaseName)

                dependsOn("assembleRelease")
            }
    }
}
