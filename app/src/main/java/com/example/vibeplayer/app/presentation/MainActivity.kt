package com.example.vibeplayer.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.vibeplayer.app.presentation.navigation.NavigationRoot
import com.example.vibeplayer.app.presentation.navigation.NavigationScreens
import com.example.vibeplayer.core.presentation.designsystem.VibePlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibePlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationRoot(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        startDestination = NavigationScreens.MainPage
                    )
                }
            }
        }
    }
}