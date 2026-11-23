package com.ergin.ghclient.feature.upload.impl.data.worker

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.upload.domain.repository.UploadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "FileUploadWorker"

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val uploadRepository: UploadRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val owner = inputData.getString(KEY_OWNER) ?: return androidx.work.ListenableWorker.Result.failure()
        val repoName = inputData.getString(KEY_REPO) ?: return androidx.work.ListenableWorker.Result.failure()
        val path = inputData.getString(KEY_PATH) ?: return androidx.work.ListenableWorker.Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: "Upload file via GHClient"
        val fileUriString = inputData.getString(KEY_FILE_URI) ?: return androidx.work.ListenableWorker.Result.failure()

        Log.d(TAG, "Starting file upload worker for $owner/$repoName path=$path")

        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(fileUriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext androidx.work.ListenableWorker.Result.failure()

                val bytes = inputStream.use { it.readBytes() }
                val base64Content = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val result = uploadRepository.uploadFile(
                    owner = OwnerName(owner),
                    repo = RepoName(repoName),
                    path = path,
                    message = message,
                    base64Content = base64Content
                )

                when (result) {
                    is com.ergin.ghclient.core.domain.Result.Success -> {
                        Log.d(TAG, "File uploaded successfully to $path")
                        androidx.work.ListenableWorker.Result.success()
                    }
                    is com.ergin.ghclient.core.domain.Result.Error -> {
                        Log.e(TAG, "File upload failed: ${result.error}")
                        androidx.work.ListenableWorker.Result.retry()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during file upload worker", e)
                androidx.work.ListenableWorker.Result.failure()
            }
        }
    }

    companion object {
        const val KEY_OWNER = "key_owner"
        const val KEY_REPO = "key_repo"
        const val KEY_PATH = "key_path"
        const val KEY_MESSAGE = "key_message"
        const val KEY_FILE_URI = "key_file_uri"
    }
}
