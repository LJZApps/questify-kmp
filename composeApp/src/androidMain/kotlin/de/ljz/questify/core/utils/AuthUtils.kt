package de.ljz.questify.core.utils

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

object AuthUtils {
    fun launchCustomTab(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()

        customTabsIntent.launchUrl(context, url.toUri())
    }
}