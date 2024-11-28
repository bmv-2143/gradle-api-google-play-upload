package net.humblegames.gradle

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.*
import java.io.File
import java.io.FileInputStream
import java.util.Collections


class GooglePlayPublisher(private val credentialsJsonPath: String) {

    fun uploadAab(
        pathToAab: String,
        trackName: String,
        packageName: String,
        rolloutPercentage: Double,
        trackReleaseName: String,
        releaseNotes: List<LocalizedText>,
    ) {
        // Set up your service account credentials
        val service = createPlayStorePublisher(credentialsJsonPath)

        // Prepare the AAB file for upload
        val aabFile = File(pathToAab)
        val aabContent = FileContent(AAB_FILE_CONTENT_TYPE, aabFile)

        // Create a new edit
        val edits = service.edits()
        val editRequest = edits.insert(packageName, null)
        val edit: AppEdit = editRequest.execute()
        val editId = edit.id

        // Upload the AAB file
        val uploadRequest = edits.bundles().upload(packageName, editId, aabContent)
        val bundle: Bundle = uploadRequest.execute()

        // Assign the uploaded bundle to the "internal" testing track
        val track = Track().setTrack(trackName).setReleases(
            listOf(
                TrackRelease().setName(trackReleaseName)
                    .setVersionCodes(listOf(bundle.versionCode.toLong()))
                    .setStatus("draft")
//                    .setUserFraction(rolloutPercentage)
                    .setReleaseNotes(releaseNotes)
            )
        )
        val trackUpdateRequest = edits.tracks().update(packageName, editId, trackName, track)
        trackUpdateRequest.execute()

        // Commit the edit
        val commitRequest = edits.commit(packageName, editId)
        val committedEdit: AppEdit = commitRequest.execute()
        println("AAB file uploaded successfully: ${bundle.versionCode}")
    }

    private fun createPlayStorePublisher(pathToCredentialsJson: String): AndroidPublisher {
        val credential =
            GoogleCredential.fromStream(FileInputStream(pathToCredentialsJson))
                .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
        val service = AndroidPublisher.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(USER_AGENT_HEADER_APP_NAME).build()
        return service
    }

    companion object {
        private const val AAB_FILE_CONTENT_TYPE = "application/octet-stream"
        private const val APK_FILE_CONTENT_TYPE = "application/vnd.android.package-archive"
        private const val USER_AGENT_HEADER_APP_NAME = "Experimental App"
    }
}
