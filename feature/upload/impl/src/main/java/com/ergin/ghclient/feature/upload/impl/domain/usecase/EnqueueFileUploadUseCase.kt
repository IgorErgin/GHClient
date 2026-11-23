package com.ergin.ghclient.feature.upload.impl.domain.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ergin.ghclient.feature.upload.impl.data.worker.FileUploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EnqueueFileUploadUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(
        owner: String,
        repo: String,
        path: String,
        message: String,
        fileUriString: String
    ) {
        val inputData = Data.Builder()
            .putString(FileUploadWorker.KEY_OWNER, owner)
            .putString(FileUploadWorker.KEY_REPO, repo)
            .putString(FileUploadWorker.KEY_PATH, path)
            .putString(FileUploadWorker.KEY_MESSAGE, message)
            .putString(FileUploadWorker.KEY_FILE_URI, fileUriString)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<FileUploadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}
