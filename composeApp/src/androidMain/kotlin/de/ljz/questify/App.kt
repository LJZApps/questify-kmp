package de.ljz.questify

import android.app.Application
import de.ljz.questify.core.data.datastore.initAndroidDataStore
import de.ljz.questify.core.di.sharedModules
import de.ljz.questify.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        initAndroidDataStore(this)

        startKoin {
            androidLogger()
            androidContext(this@App)

            modules(sharedModules + viewModelModule)
        }
    }
}