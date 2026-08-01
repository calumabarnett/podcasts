package com.podcasts.di

import com.podcasts.data.datasource.local.getDatabaseBuilder
import com.podcasts.data.datasource.local.getRoomDatabase
import com.podcasts.data.repository.PodcastRepositoryImpl
import com.podcasts.domain.player.AudioPlayer
import com.podcasts.player.AndroidAudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        getRoomDatabase(getDatabaseBuilder(androidContext()))
    }

    single<AudioPlayer> {
        AndroidAudioPlayer(
            context = androidContext(),
            repository = get() as PodcastRepositoryImpl
        )
    }
}
