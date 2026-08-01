package com.podcasts.domain.repository

import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.Podcast
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    suspend fun searchPodcasts(query: String): List<Podcast>

    suspend fun getPodcastFeed(feedUrl: String): Pair<Podcast, List<Episode>>

    fun getSubscribedPodcasts(): Flow<List<Podcast>>

    suspend fun subscribeToPodcast(podcast: Podcast)

    suspend fun unsubscribeFromPodcast(podcastId: String)

    fun isSubscribed(podcastId: String): Flow<Boolean>

    fun getEpisodesInProgress(): Flow<List<Episode>>

    suspend fun updateEpisodePlaybackProgress(episodeId: String, position: Long, isCompleted: Boolean)

    fun getEpisodePlaybackProgress(episodeId: String): Flow<Long>

    fun getEpisodeById(episodeId: String): Flow<Episode?>

    suspend fun startEpisodePlayback(episode: Episode)
}
