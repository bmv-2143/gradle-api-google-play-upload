package net.humblegames.gradle

import com.google.api.services.androidpublisher.model.LocalizedText
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface GooglePlayPublishExtension {

    val trackToPublish: Property<String>
    val releaseName: Property<String>
    val rolloutPercentage: Property<Double>
    val releaseNotes: ListProperty<LocalizedText>

}
