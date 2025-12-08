package com.example.vibeplayer.app.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface MainEvents {
    data object EndSplashScreenVisibility : MainEvents
}

class MainViewModel() : ViewModel() {
    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    fun onActions(mainEvents: MainEvents) {
        when (mainEvents) {
            is MainEvents.EndSplashScreenVisibility -> endSplashScreenVisibility()
        }
    }

    private fun endSplashScreenVisibility() {
            _mainState.update { newState ->
                newState.copy(isSplashVisible = false)
            }
    }
}