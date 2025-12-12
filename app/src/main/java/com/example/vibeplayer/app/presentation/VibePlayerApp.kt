package com.example.vibeplayer.app.presentation

import android.app.Application
import com.example.vibeplayer.BuildConfig
import com.example.vibeplayer.app.di.appModule
import com.example.vibeplayer.core.di.coreDataModule
import com.example.vibeplayer.core.CoreModule.coreModule
import com.example.vibeplayer.core.database.di.databaseModule
import com.example.vibeplayer.feature.main.di.mainModule
import com.example.vibeplayer.feature.permission.di.PermissionModule.permissionModule
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
                permissionModule,
                mainModule,
                coreDataModule,
                databaseModule
            )
        }
    }
}