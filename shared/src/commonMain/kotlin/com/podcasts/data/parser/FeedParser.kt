package com.podcasts.data.parser

import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.Podcast
import nl.adaptivity.xmlutil.serialization.XML

class FeedParser {
    private val xml = XML {
        autoPolymorphic = true
        unknownChildHandler = { _, _, _, _, _ -> emptyList() }
    }

    fun parse(xmlString: String, feedUrl: String): Pair<Podcast, List<Episode>> {
        val rss = xml.decodeFromString(RssFeed.serializer(), xmlString)
        val channel = rss.channel

        val podcastId = feedUrl
        val podcastTitle = channel.title
        val author = channel.author ?: "Unknown Author"
        val description = channel.description ?: ""
        val imageUrl = channel.image?.url ?: ""

        val podcast = Podcast(
            id = podcastId,
            title = podcastTitle,
            author = author,
            description = description,
            imageUrl = imageUrl,
            feedUrl = feedUrl
        )

        val episodes = channel.item.mapNotNull { item ->
            val audioUrl = item.enclosure?.url ?: return@mapNotNull null
            val guid = item.guid ?: audioUrl
            val publishDate = parsePubDateToEpoch(item.pubDate)
            val duration = parseDurationToSeconds(item.duration)

            Episode(
                id = guid,
                podcastId = podcastId,
                podcastTitle = podcastTitle,
                podcastImageUrl = imageUrl,
                title = item.title,
                description = item.description ?: "",
                publishDate = publishDate,
                duration = duration,
                audioUrl = audioUrl
            )
        }

        return Pair(podcast, episodes)
    }

    private fun parsePubDateToEpoch(pubDate: String?): Long {
        if (pubDate.isNullOrBlank()) return 0L
        return try {
            // Standard format: "Wed, 15 Jun 2022 13:00:00 +0000" or "15 Jun 2022 13:00:00 GMT"
            val cleanDate = if (pubDate.contains(",")) {
                pubDate.substringAfter(",").trim()
            } else {
                pubDate.trim()
            }

            val parts = cleanDate.split(" ")
            if (parts.size < 4) return 0L

            val day = parts[0].toIntOrNull() ?: 1
            val monthStr = parts[1]
            val year = parts[2].toIntOrNull() ?: 1970

            val timeParts = parts[3].split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0

            val month = when (monthStr.take(3).lowercase()) {
                "jan" -> 1
                "feb" -> 2
                "mar" -> 3
                "apr" -> 4
                "may" -> 5
                "jun" -> 6
                "jul" -> 7
                "aug" -> 8
                "sep" -> 9
                "oct" -> 10
                "nov" -> 11
                "dec" -> 12
                else -> 1
            }

            // Simple rough calculation of Unix epoch for common KMP (avoids Calendar classes)
            // Useful because we just want a sorting order and general date
            val daysInMonths = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            var yearDays = 0L
            for (y in 1970 until year) {
                yearDays += if ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) 366 else 365
            }

            var monthDays = 0L
            for (m in 1 until month) {
                monthDays += daysInMonths[m]
                if (m == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) {
                    monthDays += 1
                }
            }

            val totalDays = yearDays + monthDays + (day - 1)
            val totalSeconds = (totalDays * 86400) + (hour * 3600) + (minute * 60) + second

            totalSeconds * 1000 // Milliseconds
        } catch (e: Exception) {
            0L
        }
    }

    private fun parseDurationToSeconds(duration: String?): Long {
        if (duration.isNullOrBlank()) return 0L
        return try {
            if (duration.contains(":")) {
                val parts = duration.split(":")
                when (parts.size) {
                    2 -> {
                        val min = parts[0].toLongOrNull() ?: 0L
                        val sec = parts[1].toLongOrNull() ?: 0L
                        (min * 60) + sec
                    }
                    3 -> {
                        val hr = parts[0].toLongOrNull() ?: 0L
                        val min = parts[1].toLongOrNull() ?: 0L
                        val sec = parts[2].toLongOrNull() ?: 0L
                        (hr * 3600) + (min * 60) + sec
                    }
                    else -> 0L
                }
            } else {
                duration.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}
