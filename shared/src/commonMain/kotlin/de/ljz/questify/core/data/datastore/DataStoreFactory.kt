package de.ljz.questify.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

internal fun <T> createDataStore(
    producePath: () -> String,
    serializer: OkioSerializer<T>,
    defaultValue: T
): DataStore<T> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = serializer,
            producePath = { producePath().toPath() }
        ),
        corruptionHandler = null,
        migrations = emptyList(),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )
}

expect fun dataStorePreferencesPath(fileName: String): String