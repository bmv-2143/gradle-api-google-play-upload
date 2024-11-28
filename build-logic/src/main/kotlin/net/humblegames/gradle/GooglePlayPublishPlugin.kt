package net.humblegames.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class GooglePlayPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException("Android not found")

        val googlePlayPublishExtension =
            project.extensions.create(
                "googlePlayPublishExtension",
                GooglePlayPublishExtension::class.java,
            )

        androidComponents.onVariants { variant ->
            if (variant.name == "release") {
                project.tasks.register(
                    "myGooglePlayPublishTask",
                    GooglePlayPublishTask::class.java
                ) {
                    group = "google play group"
                    description = "This is Google Play Publish Task"

                    trackToPublish.set(googlePlayPublishExtension.trackToPublish)
                    releaseName.set(googlePlayPublishExtension.releaseName)
                    rolloutPercentage.set(googlePlayPublishExtension.rolloutPercentage)
                    releaseNotes.set(googlePlayPublishExtension.releaseNotes)
                    status.set(googlePlayPublishExtension.status)

                    aabDir.set(
                        project.layout.dir(
                            variant.artifacts.get(SingleArtifact.BUNDLE).map { it.asFile.parentFile }
                        )
                    )
                }
            }
        }
    }
}
