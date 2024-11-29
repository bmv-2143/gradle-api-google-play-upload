package net.humblegames.gradle

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface GooglePlayPublishExtension {

    val credentialsJsonPath: Property<String>
    val trackToPublish: Property<String>
    val releaseName: Property<String>
    val rolloutPercentage: Property<Double>
    val releaseNotes: MapProperty<String, String> // Language code to text
    val status: Property<String>

}
