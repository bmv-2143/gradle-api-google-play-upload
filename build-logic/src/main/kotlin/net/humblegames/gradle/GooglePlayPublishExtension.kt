package net.humblegames.gradle

import org.gradle.api.provider.Property

interface GooglePlayPublishExtension {

    val trackToPublish: Property<String>
    val releaseName: Property<String>

}

// works
//open class GooglePlayPublishExtension(project : Project) {
//
//    var trackToPublish: Property<String> = project.objects.property(String::class.java).value("internal")
//    var releaseName: Property<String> = project.objects.property(String::class.java).value("release-")
//
//}