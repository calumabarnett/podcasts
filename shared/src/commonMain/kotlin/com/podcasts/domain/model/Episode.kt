package com.podcasts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val id: String,
    val podcastId: String,
    val podcastTitle: String,
    val podcastImageUrl: String,
    val title: String,
    val description: String,
    val publishDate: Long,
    val duration: Long,
    val audioUrl: String,
    val playbackPosition: Long = 0,
    val isCompleted: Boolean = false
)
