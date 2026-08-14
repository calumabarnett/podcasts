package com.podcasts.data.network

import com.podcasts.utils.getCurrentTimeSeconds
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8

class PodcastIndexClient(
    private val apiKey: String,
    private val apiSecret: String,
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
) {
    private val mockFeeds = listOf(
        PodcastIndexFeed(
            id = 1,
            title = "The Daily",
            url = "https://feeds.simplecast.com/54nAGgIl", // Real functional RSS Feed
            author = "The New York Times",
            description = "This is what the news should sound like. The biggest stories of our time, told by the best journalists in the world. Hosted by Michael Barbaro and Sabrina Tavernise.",
            image = "https://images.squarespace-cdn.com/content/v1/5e73bc5b4cb5f5195e263c9b/1586791242300-34XCHH6S6V9S0K9P5YF3/The+Daily.png"
        ),
        PodcastIndexFeed(
            id = 2,
            title = "Lex Fridman Podcast",
            url = "https://lexfridman.com/feed/podcast/",
            author = "Lex Fridman",
            description = "Conversations about science, technology, history, philosophy and the nature of intelligence, consciousness, love, and power.",
            image = "https://lexfridman.com/wordpress/wp-content/uploads/cropped-art-3.jpg"
        ),
        PodcastIndexFeed(
            id = 3,
            title = "Huberman Lab",
            url = "https://feeds.megaphone.fm/hubermanlab",
            author = "Scicomm Media",
            description = "Dr. Andrew Huberman discusses neuroscience: how our brain and its connections with the organs of our body control our perceptions, our behaviors, and our health.",
            image = "https://images.megaphone.fm/FMDp4228966779"
        ),
        PodcastIndexFeed(
            id = 4,
            title = "Planet Money",
            url = "https://feeds.npr.org/510289/podcast.xml",
            author = "NPR",
            description = "The economy explained. Imagine you could call up a friend and say, 'Meet me at the bar and tell me what's going on with the economy.' Now imagine that's actually a fun conversation.",
            image = "https://media.npr.org/images/podcasts/primary/icon_510289.png"
        ),
        PodcastIndexFeed(
            id = 5,
            title = "Stuff You Should Know",
            url = "https://feeds.megaphone.fm/stuffyoushouldknow",
            author = "iHeartPodcasts",
            description = "Join Josh and Chuck as they explore the Stuff You Should Know about everything from Genes to the Grand Canyon in this award-winning podcast.",
            image = "https://images.megaphone.fm/GLT3680327685"
        )
    )

    suspend fun searchPodcasts(query: String): List<PodcastIndexFeed> {
        if (apiKey.isBlank() || apiSecret.isBlank()) {
            return filterMockFeeds(query)
        }

        return try {
            val epoch = getCurrentTimeSeconds()
            val authString = "$apiKey$apiSecret$epoch"
            val authorization = authString.encodeUtf8().sha1().hex()

            val response: PodcastIndexResponse = httpClient.get("https://api.podcastindex.org/api/1.0/search/byterm") {
                parameter("q", query)
                header("User-Agent", "MinimalistPodcasts/1.0")
                header("X-Auth-Key", apiKey)
                header("X-Auth-Date", epoch.toString())
                header("Authorization", authorization)
            }.body()

            if (response.feeds.isEmpty() && response.status == "true") {
                emptyList()
            } else {
                response.feeds
            }
        } catch (e: Exception) {
            filterMockFeeds(query)
        }
    }

    private fun filterMockFeeds(query: String): List<PodcastIndexFeed> {
        if (query.isBlank()) return mockFeeds
        return mockFeeds.filter {
            it.title.contains(query, ignoreCase = true) ||
            (it.author?.contains(query, ignoreCase = true) == true) ||
            (it.description?.contains(query, ignoreCase = true) == true)
        }
    }
}
