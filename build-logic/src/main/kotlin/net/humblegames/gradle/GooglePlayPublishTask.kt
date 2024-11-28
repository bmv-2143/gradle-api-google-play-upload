package net.humblegames.gradle

import com.android.build.gradle.AppExtension
import com.google.api.services.androidpublisher.model.LocalizedText
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
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

    @get:Input
    abstract val rolloutPercentage: Property<Double>

    @get:Input
    abstract val releaseNotes: ListProperty<LocalizedText>

    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputDirectory
    abstract val aabDir: DirectoryProperty

    @TaskAction
    fun act() {
        val publisher = GooglePlayPublisher(
            "credentials/gradle-upload-apk-to-play.json"
        )
        publisher.uploadAab(
            pathToAab = aabDir.get().asFile.resolve("app-release.aab").path,
            trackName = trackToPublish.get(),
            packageName = getApplicationId(project),
            rolloutPercentage = rolloutPercentage.get(),
            trackReleaseName = releaseName.get(),
            releaseNotes = releaseNotes.get(),
            status = "draft"
        )
    }

    private fun getApplicationId(project: Project) =
        project.extensions.getByType(AppExtension::class.java)
            .defaultConfig.applicationId ?: throw GradleException("ApplicationId not found")
}
