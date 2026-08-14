package com.podcasts.data.repository

import com.podcasts.data.datasource.local.EpisodeProgressEntity
import com.podcasts.data.datasource.local.PodcastDatabase
import com.podcasts.data.datasource.local.PodcastEntity
import com.podcasts.data.network.PodcastIndexClient
import com.podcasts.data.parser.FeedParser
import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.Podcast
import com.podcasts.domain.repository.PodcastRepository
import com.podcasts.utils.getCurrentTimeSeconds
import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.*

class PodcastRepositoryImpl(
    private val podcastIndexClient: PodcastIndexClient,
    private val feedParser: FeedParser,
    private val database: PodcastDatabase,
    private val httpClient: HttpClient = HttpClient()
) : PodcastRepository {

    private val podcastDao = database.podcastDao()
    private val progressDao = database.episodeProgressDao()

    override suspend fun searchPodcasts(query: String): List<Podcast> {
        return podcastIndexClient.searchPodcasts(query).map { feed ->
            Podcast(
                id = feed.url,
                title = feed.title,
                author = feed.author ?: "Unknown",
                description = feed.description ?: "",
                imageUrl = feed.image ?: feed.artwork ?: "",
                feedUrl = feed.url
            )
        }
    }

    override suspend fun getPodcastFeed(feedUrl: String): Pair<Podcast, List<Episode>> {
        val xmlString = httpClient.get(feedUrl).bodyAsText()
        val (podcast, episodes) = feedParser.parse(xmlString, feedUrl)

        // Match with local progress so positions are accurate
        val enrichedEpisodes = episodes.map { episode ->
            val localProgress = progressDao.getEpisodeProgress(episode.id).firstOrNull()
            if (localProgress != null) {
                episode.copy(
                    playbackPosition = localProgress.playbackPosition,
                    isCompleted = localProgress.isCompleted
                )
            } else {
                episode
            }
        }

        return Pair(podcast, enrichedEpisodes)
    }

    override fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return podcastDao.getSubscribedPodcasts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun subscribeToPodcast(podcast: Podcast) {
        podcastDao.insertPodcast(podcast.toEntity())
    }

    override suspend fun unsubscribeFromPodcast(podcastId: String) {
        podcastDao.deletePodcast(podcastId)
    }

    override fun isSubscribed(podcastId: String): Flow<Boolean> {
        return podcastDao.isSubscribed(podcastId)
    }

    override fun getEpisodesInProgress(): Flow<List<Episode>> {
        return progressDao.getEpisodesInProgress().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateEpisodePlaybackProgress(
        episodeId: String,
        position: Long,
        isCompleted: Boolean
    ) {
        // We first need to query the existing episode details so we don't overwrite other fields with empty values
        val existingProgress = progressDao.getEpisodeProgress(episodeId).firstOrNull()
        if (existingProgress != null) {
            val updated = existingProgress.copy(
                playbackPosition = position,
                isCompleted = isCompleted,
                lastUpdated = getCurrentTimeSeconds()
            )
            progressDao.insertOrUpdateProgress(updated)
        }
    }

    // Helper method to insert progress when playback begins
    override suspend fun startEpisodePlayback(episode: Episode) {
        val existingProgress = progressDao.getEpisodeProgress(episode.id).firstOrNull()
        val currentPosition = existingProgress?.playbackPosition ?: episode.playbackPosition
        val isCompleted = existingProgress?.isCompleted ?: episode.isCompleted

        val progressEntity = EpisodeProgressEntity(
            id = episode.id,
            podcastId = episode.podcastId,
            podcastTitle = episode.podcastTitle,
            podcastImageUrl = episode.podcastImageUrl,
            title = episode.title,
            description = episode.description,
            publishDate = episode.publishDate,
            duration = episode.duration,
            audioUrl = episode.audioUrl,
            playbackPosition = currentPosition,
            isCompleted = isCompleted,
            lastUpdated = getCurrentTimeSeconds()
        )
        progressDao.insertOrUpdateProgress(progressEntity)
    }

    override fun getEpisodePlaybackProgress(episodeId: String): Flow<Long> {
        return progressDao.getPlaybackPosition(episodeId).map { it ?: 0L }
    }

    override fun getEpisodeById(episodeId: String): Flow<Episode?> {
        return progressDao.getEpisodeProgress(episodeId).map { it?.toDomain() }
    }

    private fun Podcast.toEntity() = PodcastEntity(
        id = id,
        title = title,
        author = author,
        description = description,
        imageUrl = imageUrl,
        feedUrl = feedUrl
    )

    private fun PodcastEntity.toDomain() = Podcast(
        id = id,
        title = title,
        author = author,
        description = description,
        imageUrl = imageUrl,
        feedUrl = feedUrl
    )

    private fun EpisodeProgressEntity.toDomain() = Episode(
        id = id,
        podcastId = podcastId,
        podcastTitle = podcastTitle,
        podcastImageUrl = podcastImageUrl,
        title = title,
        description = description,
        publishDate = publishDate,
        duration = duration,
        audioUrl = audioUrl,
        playbackPosition = playbackPosition,
        isCompleted = isCompleted
    )
}
