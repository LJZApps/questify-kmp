package de.ljz.questify.core.data.datastore

import android.content.Context
import java.io.File

private lateinit var androidContext: Context

fun initAndroidDataStore(context: Context) {
    androidContext = context
}

actual fun dataStorePreferencesPath(fileName: String): String {
    return File(androidContext.filesDir, "datastore/$fileName").absolutePath
}