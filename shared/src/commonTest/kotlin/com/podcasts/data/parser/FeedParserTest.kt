package com.podcasts.data.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FeedParserTest {

    @Test
    fun testParseValidPodcastRssFeed() {
        val xmlString = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
              <channel>
                <title>Test Podcast</title>
                <description>A podcast for automated tests.</description>
                <author>Jules</author>
                <image>
                  <url>https://example.com/podcast.jpg</url>
                </image>
                <item>
                  <title>Episode 1: The Beginning</title>
                  <description>First episode description.</description>
                  <pubDate>Wed, 15 Jun 2022 13:00:00 +0000</pubDate>
                  <enclosure url="https://example.com/episode1.mp3" type="audio/mpeg" length="12345" />
                  <guid>ep1-guid</guid>
                  <itunes:duration>00:45:00</itunes:duration>
                </item>
                <item>
                  <title>Episode 2: The Middle</title>
                  <description>Second episode description.</description>
                  <pubDate>Thu, 16 Jun 2022 14:30:00 +0000</pubDate>
                  <enclosure url="https://example.com/episode2.mp3" type="audio/mpeg" length="54321" />
                  <guid>ep2-guid</guid>
                  <itunes:duration>01:15:30</itunes:duration>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val parser = FeedParser()
        val (podcast, episodes) = parser.parse(xmlString, "https://example.com/feed.xml")

        assertEquals("https://example.com/feed.xml", podcast.id)
        assertEquals("Test Podcast", podcast.title)
        assertEquals("A podcast for automated tests.", podcast.description)
        assertEquals("https://example.com/podcast.jpg", podcast.imageUrl)

        assertEquals(2, episodes.size)

        val ep1 = episodes[0]
        assertEquals("ep1-guid", ep1.id)
        assertEquals("https://example.com/feed.xml", ep1.podcastId)
        assertEquals("Episode 1: The Beginning", ep1.title)
        assertEquals("https://example.com/episode1.mp3", ep1.audioUrl)
        assertEquals(2700L, ep1.duration) // 45 * 60

        val ep2 = episodes[1]
        assertEquals("ep2-guid", ep2.id)
        assertEquals("Episode 2: The Middle", ep2.title)
        assertEquals("https://example.com/episode2.mp3", ep2.audioUrl)
        assertEquals(4530L, ep2.duration) // 1 * 3600 + 15 * 60 + 30
    }
}
