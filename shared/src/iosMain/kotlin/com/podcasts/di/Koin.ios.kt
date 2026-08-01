package com.podcasts.di

import com.podcasts.data.datasource.local.getDatabaseBuilder
import com.podcasts.data.datasource.local.getRoomDatabase
import com.podcasts.domain.player.AudioPlayer
import com.podcasts.domain.model.PlaybackState
import com.podcasts.domain.model.Episode
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual val platformModule: Module = module {
    single {
        getRoomDatabase(getDatabaseBuilder())
    }

    single<AudioPlayer> {
        object : AudioPlayer {
            private val _state = MutableStateFlow(PlaybackState())
            override val playbackState: StateFlow<PlaybackState> = _state
            override fun play(episode: Episode) {}
            override fun resume() {}
            override fun pause() {}
            override fun seekTo(positionMs: Long) {}
            override fun skipForward() {}
            override fun skipBackward() {}
            override fun setPlaybackSpeed(speed: Float) {}
            override fun release() {}
        }
    }
}
