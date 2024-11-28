package net.humblegames.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
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
                    rolloutPercentage.set(googlePlayPublishExtension.rolloutPercentage)
                    releaseNotes.set(googlePlayPublishExtension.releaseNotes)
                    aabDir.set(artifacts)

                    val publisher = GooglePlayPublisher(
                        "credentials/gradle-upload-apk-to-play.json"
                    )

                    doLast {
                        publisher.uploadAab(
                            pathToAab = artifacts.get().asFile.resolve("app-release.aab").path,
                            trackName = trackToPublish.get(),
                            packageName = getApplicationId(project),
                            rolloutPercentage = rolloutPercentage.get(),
                            trackReleaseName = releaseName.get(),
                            releaseNotes = releaseNotes.get(),
                            status = "draft"
                        )
                    }
                }

                publishTask.configure {
                    dependsOn("bundle${variant.name.capitalize()}") // => "bundleRelease"
                }
            }

        }

    }

    private fun getApplicationId(project: Project) =
        project.extensions.getByType(AppExtension::class.java)
            .defaultConfig.applicationId ?: throw GradleException("ApplicationId not found")
}
