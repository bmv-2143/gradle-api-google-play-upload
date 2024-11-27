package net.humblegames.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

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
            val artifacts: Provider<Directory> =
                variant.artifacts.get(SingleArtifact.BUNDLE).flatMap { bundleFile ->
                    project.layout.dir(
                        project.provider { bundleFile.asFile.parentFile }
                    )
                }

            if (variant.name == "release") {
                val publishTask = project.tasks.register(
                    "myGooglePlayPublishTask",
                    GooglePlayPublishTask::class.java
                ) {
                    group = "google play group"
                    description = "This is Google Play Publish Task"
                    trackToPublish.set(googlePlayPublishExtension.trackToPublish)
                    releaseName.set(googlePlayPublishExtension.releaseName)

                    aabDir.set(artifacts)
                }

                publishTask.configure {
                    dependsOn("bundleRelease")
//                    dependsOn("bundle${variant.name.capitalize()}")
                }
            }

        }

    }
}
