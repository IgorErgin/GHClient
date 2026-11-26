package com.ergin.ghclient.feature.issues.impl.data.local

import android.content.Context
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueDraftFileStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : IssueDraftFileStorage {

    private val file: File
        get() = File(context.filesDir, "issue_drafts_cache.json")

    private val mutex = Mutex()

    override suspend fun loadAll(): List<IssueDraftDto> = withContext(Dispatchers.IO) {
        mutex.withLock {
            readFromFileInternal()
        }
    }

    override suspend fun saveAll(items: List<IssueDraftDto>): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            writeToFileInternal(items)
        }
    }

    override suspend fun getById(uid: String): IssueDraftDto? = withContext(Dispatchers.IO) {
        mutex.withLock {
            readFromFileInternal().firstOrNull { it.uid == uid }
        }
    }

    override suspend fun save(item: IssueDraftDto): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            val items = readFromFileInternal().toMutableList()
            val index = items.indexOfFirst { it.uid == item.uid }
            if (index >= 0) {
                items[index] = item
            } else {
                items.add(item)
            }
            writeToFileInternal(items)
        }
    }

    override suspend fun delete(uid: String): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            val items = readFromFileInternal().filterNot { it.uid == uid }
            writeToFileInternal(items)
        }
    }

    private fun readFromFileInternal(): List<IssueDraftDto> {
        return try {
            if (!file.exists()) return emptyList()
            val content = file.readText()
            if (content.isBlank()) return emptyList()
            json.decodeFromString<List<IssueDraftDto>>(content)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }
    }

    private fun writeToFileInternal(items: List<IssueDraftDto>) {
        try {
            val content = json.encodeToString(items)
            file.writeText(content)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }
}
