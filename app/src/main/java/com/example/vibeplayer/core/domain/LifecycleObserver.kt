package com.example.vibeplayer.core.domain

import kotlinx.coroutines.flow.Flow

interface LifecycleObserver {
    val isInForeground: Flow<Boolean>
}