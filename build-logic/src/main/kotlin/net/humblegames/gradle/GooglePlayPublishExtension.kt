package net.humblegames.gradle

import com.google.api.services.androidpublisher.model.LocalizedText
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface GooglePlayPublishExtension {

    val credentialsJsonPath: Property<String>
    val trackToPublish: Property<String>
    val releaseName: Property<String>
    val rolloutPercentage: Property<Double>
    val releaseNotes: ListProperty<LocalizedText>
    val status: Property<String>

}
