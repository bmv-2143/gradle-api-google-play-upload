package net.humblegames.gradle

import org.gradle.api.provider.Property

interface GooglePlayPublishExtension {

    val trackToPublish: Property<String>
    val releaseName: Property<String>

}
