package com.example.vibeplayer.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.vibeplayer.app.presentation.navigation.NavigationRoot
import com.example.vibeplayer.app.presentation.navigation.NavigationScreens
import com.example.vibeplayer.core.presentation.designsystem.VibePlayerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.mainState.value.isSplashVisible
        }
        setContent {
            VibePlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(!mainViewModel.mainState.value.isSplashVisible) {
                        NavigationRoot(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            startDestination = NavigationScreens.MainPage
                        )
                    }
                }
            }
        }
    }
}