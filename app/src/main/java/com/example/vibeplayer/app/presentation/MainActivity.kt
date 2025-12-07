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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            //you should not call state flow value directly inside of a composable because it
            //will not trigger recomposition when a flow emits a new value.
            //collectAsState should be used
            //also, here we need to check if permission is granted or not, and based on that
            //pass Boolean value to the NavigationRoot that will decide which screen to show
            val state = mainViewModel.mainState.collectAsStateWithLifecycle()
            VibePlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!state.value.isSplashVisible) {
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