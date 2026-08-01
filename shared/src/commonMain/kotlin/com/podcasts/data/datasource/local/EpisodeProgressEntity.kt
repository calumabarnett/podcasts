package com.podcasts.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_progress")
data class EpisodeProgressEntity(
    @PrimaryKey val id: String,
    val podcastId: String,
    val podcastTitle: String,
    val podcastImageUrl: String,
    val title: String,
    val description: String,
    val publishDate: Long,
    val duration: Long,
    val audioUrl: String,
    val playbackPosition: Long,
    val isCompleted: Boolean,
    val lastUpdated: Long
)
