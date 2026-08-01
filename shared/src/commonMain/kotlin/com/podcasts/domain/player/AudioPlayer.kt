package com.podcasts.domain.player

import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.PlaybackState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val playbackState: StateFlow<PlaybackState>

    fun play(episode: Episode)
    fun resume()
    fun pause()
    fun seekTo(positionMs: Long)
    fun skipForward()
    fun skipBackward()
    fun setPlaybackSpeed(speed: Float)
    fun release()
}
