package com.example.vibeplayer.feature.permission.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PermissionEvents {
    data object NavigateMainPage : PermissionEvents
}

sealed interface PermissionActions {
    data object NavigateMainPage : PermissionActions
    data class ShowDialog(val showDialog: Boolean): PermissionActions
}

class PermissionViewModel() : ViewModel() {
    private val _permissionUiState = MutableStateFlow(PermissionUiState())
    val permissionUiState = _permissionUiState.asStateFlow()

    private val _permissionEvents = Channel<PermissionEvents>()
    val permissionEvents = _permissionEvents.receiveAsFlow()

    fun onActions(permissionActions: PermissionActions) {
        when (permissionActions) {
            is PermissionActions.NavigateMainPage -> navigateMainPage()
            is PermissionActions.ShowDialog -> showDialog(permissionActions.showDialog)
        }
    }


    private fun navigateMainPage() {
        viewModelScope.launch {
            _permissionEvents.send(PermissionEvents.NavigateMainPage)
        }
    }

    private fun showDialog(showDialog: Boolean){
        _permissionUiState.update { newState->
            newState.copy(showDialog = showDialog)
        }
    }
}