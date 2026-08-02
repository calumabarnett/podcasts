package com.podcasts.data.datasource.local

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [PodcastEntity::class, EpisodeProgressEntity::class], version = 1)
@ConstructedBy(PodcastDatabaseConstructor::class)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeProgressDao(): EpisodeProgressDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PodcastDatabaseConstructor : RoomDatabaseConstructor<PodcastDatabase>

fun getRoomDatabase(builder: RoomDatabase.Builder<PodcastDatabase>): PodcastDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
