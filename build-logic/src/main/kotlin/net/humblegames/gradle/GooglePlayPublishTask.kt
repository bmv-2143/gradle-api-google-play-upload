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
import org.gradle.api.tasks.Optional
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
    abstract val status: Property<String>

    @get:Optional
    @get:Input
    abstract val rolloutPercentage: Property<Double>

    @get:Input
    abstract val releaseNotes: ListProperty<LocalizedText>

    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputDirectory
    abstract val aabDir: DirectoryProperty

    @get:Input
    abstract val credentialsJsonPath: Property<String>

    @TaskAction
    fun act() {
        val publisher = GooglePlayPublisher(
            credentialsJsonPath.get(),
            getApplicationId(project),
        )
        publisher.uploadAab(
            pathToAab = aabDir.get().asFile.resolve("app-release.aab").path,
            trackName = trackToPublish.get(),
            rolloutPercentage = if (rolloutPercentage.isPresent) rolloutPercentage.get() else null,
            trackReleaseName = releaseName.get(),
            releaseNotes = releaseNotes.get(),
            status = status.get(),
        )
    }

    private fun getApplicationId(project: Project) =
        project.extensions.getByType(AppExtension::class.java)
            .defaultConfig.applicationId ?: throw GradleException("ApplicationId not found")
}
