package net.humblegames.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.google.api.services.androidpublisher.model.LocalizedText
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

                    val publisher = GooglePlayPublisher(
                        "credentials/gradle-upload-apk-to-play.json"
                    )

                    doLast {
                        publisher.uploadAab(
//                            pathToAab = artifacts.get().asFile.resolve(renamedAabPath).path,
                            pathToAab = artifacts.get().asFile.resolve("app-release.aab").path,
                            trackName = "internal",
                            packageName = "net.humblegames.hw_gradle_api",
                            rolloutPercentage = 0.0,
                            trackReleaseName = "Release Name From Gradle",
                            releaseNotes = listOf(
                                LocalizedText().setLanguage("en-US")
                                    .setText("Initial release for internal testing from Gradle Script."),
                            )
                        )
                    }
                }

                publishTask.configure {
                    dependsOn("bundle${variant.name.capitalize()}") // => "bundleRelease"
                }
            }

        }

    }
}
