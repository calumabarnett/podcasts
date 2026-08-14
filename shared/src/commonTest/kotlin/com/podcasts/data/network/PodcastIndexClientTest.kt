package com.podcasts.data.network

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PodcastIndexClientTest {

    @Test
    fun testSearchPodcastsFallbackMockMode() = runTest {
        val client = PodcastIndexClient(apiKey = "", apiSecret = "")

        val results = client.searchPodcasts("")
        assertEquals(5, results.size)

        val dailyResults = client.searchPodcasts("Daily")
        assertEquals(1, dailyResults.size)
        assertEquals("The Daily", dailyResults[0].title)
        assertEquals("The New York Times", dailyResults[0].author)
    }

    @Test
    fun testSearchPodcastsFilterNoResults() = runTest {
        val client = PodcastIndexClient(apiKey = "", apiSecret = "")

        val results = client.searchPodcasts("NonExistentPodcastNameForTest")
        assertTrue(results.isEmpty())
    }
}
