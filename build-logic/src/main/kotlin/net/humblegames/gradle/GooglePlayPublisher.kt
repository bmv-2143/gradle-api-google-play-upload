package net.humblegames.gradle

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.*
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.File
import java.io.FileInputStream
import java.util.Collections


class GooglePlayPublisher(
    private val credentialsJsonPath: String,
    private val packageName: String
) {

    private val androidPublisher: AndroidPublisher = run {
        val credential = ServiceAccountCredentials
            .fromStream(FileInputStream(credentialsJsonPath))
            .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
        AndroidPublisher.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credential)
        ).setApplicationName(packageName).build()
    }

    fun uploadAab(
        pathToAab: String,
        trackName: String,
        rolloutPercentage: Double?,
        trackReleaseName: String,
        releaseNotes: List<LocalizedText>,
        status: String,
    ) {
        val editId = createEdit(androidPublisher, packageName)
        val bundle = uploadBundle(androidPublisher, packageName, editId, pathToAab)
        updateTrack(
            editId,
            trackName,
            trackReleaseName,
            bundle.versionCode.toLong(),
            rolloutPercentage,
            releaseNotes,
            status
        )
        commitEdit(packageName, editId)
        println("AAB file uploaded successfully: ${bundle.versionCode}")
    }

    private fun createEdit(service: AndroidPublisher, packageName: String): String {
        val editRequest = service.edits().insert(packageName, null)
        val edit: AppEdit = editRequest.execute()
        return edit.id
    }

    private fun uploadBundle(
        service: AndroidPublisher,
        packageName: String,
        editId: String,
        pathToAab: String
    ): Bundle {
        val aabFile = File(pathToAab)
        val aabContent = FileContent(AAB_FILE_CONTENT_TYPE, aabFile)
        val uploadRequest = service.edits().bundles().upload(packageName, editId, aabContent)
        return uploadRequest.execute()
    }

    private fun updateTrack(
        editId: String,
        trackName: String,
        trackReleaseName: String,
        versionCode: Long,
        rolloutPercentage: Double?,
        releaseNotes: List<LocalizedText>,
        status: String
    ) {
        val track = Track().setTrack(trackName).setReleases(
            listOf(
                TrackRelease().setName(trackReleaseName)
                    .setVersionCodes(listOf(versionCode))
                    .setStatus(status)
                    .setUserFraction(rolloutPercentage)
                    .setReleaseNotes(releaseNotes)
            )
        )
        val trackUpdateRequest = androidPublisher.edits().tracks()
            .update(packageName, editId, trackName, track)
        trackUpdateRequest.execute()
    }

    private fun commitEdit(packageName: String, editId: String) {
        val commitRequest = androidPublisher.edits().commit(packageName, editId)
        commitRequest.execute()
    }

    companion object {
        private const val AAB_FILE_CONTENT_TYPE = "application/octet-stream"
    }
}
