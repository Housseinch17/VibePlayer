package com.example.vibeplayer.core.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.vibeplayer.core.domain.LifecycleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class AndroidAppLifecycleObserver: LifecycleObserver {
    override val isInForeground: Flow<Boolean> = callbackFlow {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                //here only send when onResume
                Lifecycle.Event.ON_RESUME -> {
                        trySend(true)
                }
                else -> trySend(false)
            }
        }
        lifecycle.addObserver(observer)
        awaitClose { lifecycle.removeObserver(observer) }
    }.flowOn(Dispatchers.Main)
}