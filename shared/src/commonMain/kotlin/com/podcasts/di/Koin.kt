package com.podcasts.di

import com.podcasts.data.network.PodcastIndexClient
import com.podcasts.data.parser.FeedParser
import com.podcasts.data.repository.PodcastRepositoryImpl
import com.podcasts.domain.repository.PodcastRepository
import com.podcasts.shared.BuildKonfig
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }

    single {
        PodcastIndexClient(
            apiKey = BuildKonfig.PODCAST_INDEX_API_KEY,
            apiSecret = BuildKonfig.PODCAST_INDEX_API_SECRET,
            httpClient = get()
        )
    }

    single { FeedParser() }

    single<PodcastRepository> {
        PodcastRepositoryImpl(
            podcastIndexClient = get(),
            feedParser = get(),
            database = get(),
            httpClient = get()
        )
    }
}

expect val platformModule: Module
