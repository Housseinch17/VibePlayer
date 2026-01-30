package com.example.vibeplayer.app.presentation

import android.app.Application
import com.example.vibeplayer.BuildConfig
import com.example.vibeplayer.app.di.appModule
import com.example.vibeplayer.core.di.coreModule
import com.example.vibeplayer.feature.di.featureModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class VibePlayerApp : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(tree = Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@VibePlayerApp)
            modules(
                appModule,
                coreModule,
                featureModule
            )
        }
    }
}