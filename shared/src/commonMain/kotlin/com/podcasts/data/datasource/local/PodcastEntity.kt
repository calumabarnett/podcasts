package com.podcasts.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_podcasts")
data class PodcastEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val imageUrl: String,
    val feedUrl: String
)
