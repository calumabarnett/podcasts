package com.podcasts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackState(
    val currentEpisode: Episode? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val speed: Float = 1.0f
)
