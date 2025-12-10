package com.example.vibeplayer.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.domain.Result
import com.example.vibeplayer.app.domain.SongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    songRepository: SongRepository
) : ViewModel() {

    val state: StateFlow<MainState> = combine(
        songRepository.getSongs(),
        songRepository.syncSongsIfEmpty()
    ) { songs, syncResult ->
        val isLoading = syncResult is Result.Loading || (syncResult is Result.Success && songs.isEmpty())
        MainState(isLoading = isLoading, songs = songs)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainState(isLoading = true, songs = null)
        )

}