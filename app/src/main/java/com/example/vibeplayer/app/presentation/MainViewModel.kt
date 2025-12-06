package com.example.vibeplayer.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

sealed interface MainEvents {
    data class UpdateSplashScreenVisibility(val isSplashVisible: Boolean) : MainEvents
}

class MainViewModel() : ViewModel() {
    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {
        updateSplashScreenVisibility(false)
    }

    fun onActions(mainEvents: MainEvents) {
        when (mainEvents) {
            is MainEvents.UpdateSplashScreenVisibility -> updateSplashScreenVisibility(
                isSplashVisible = mainEvents.isSplashVisible
            )
        }
    }

    private fun updateSplashScreenVisibility(isSplashVisible: Boolean) {
        viewModelScope.launch {
            _mainState.update { newState ->
                delay(3.seconds)
                newState.copy(isSplashVisible = isSplashVisible)
            }
        }
    }
}