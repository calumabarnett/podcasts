package com.podcasts.data.network

import kotlinx.serialization.Serializable

@Serializable
data class PodcastIndexResponse(
    val status: String? = null,
    val feeds: List<PodcastIndexFeed> = emptyList(),
    val count: Int = 0
)

@Serializable
data class PodcastIndexFeed(
    val id: Long,
    val title: String,
    val url: String,
    val author: String? = null,
    val description: String? = null,
    val image: String? = null,
    val artwork: String? = null
)
