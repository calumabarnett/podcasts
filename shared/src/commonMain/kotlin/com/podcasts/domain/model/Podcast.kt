package com.podcasts.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Podcast(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val imageUrl: String,
    val feedUrl: String
)
