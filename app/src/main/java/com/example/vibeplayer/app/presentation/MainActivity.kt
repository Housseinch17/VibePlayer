package com.example.vibeplayer.app.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.app.presentation.navigation.NavigationRoot
import com.example.vibeplayer.app.presentation.navigation.NavigationScreens
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerTheme
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG

import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModel<MainViewModel>()
    private var hasGranted: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            //false → icons become white, background become dark
            //true -> icons become dark, background become light
            isAppearanceLightStatusBars = false
        }
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.mainState.value.isSplashVisible
        }
        //check if media file/storage permission granted
        hasGranted = checkMediaPermission()
        setContent {
            VibePlayerTheme {
                //End SplashScreen visibility
                mainViewModel.onActions(MainEvents.EndSplashScreenVisibility)
                val state by mainViewModel.mainState.collectAsStateWithLifecycle()
                val startDestination =
                    if (hasGranted) NavigationScreens.MainPage else NavigationScreens.Permission
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surfaceBG
                ) { innerPadding ->
                    if (!state.isSplashVisible) {
                        NavigationRoot(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }

    internal val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    internal fun checkMediaPermission(): Boolean {
        return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}