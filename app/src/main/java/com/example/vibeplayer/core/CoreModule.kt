package com.example.vibeplayer.core

import com.example.vibeplayer.core.data.AndroidAppLifecycleObserver
import com.example.vibeplayer.core.domain.LifecycleObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object CoreModule {
    val coreModule = module {
        singleOf(::AndroidAppLifecycleObserver).bind<LifecycleObserver>()
    }
}