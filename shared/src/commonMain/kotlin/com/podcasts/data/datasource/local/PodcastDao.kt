package com.podcasts.data.datasource.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {
    @Query("SELECT * FROM subscribed_podcasts")
    fun getSubscribedPodcasts(): Flow<List<PodcastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcast(podcast: PodcastEntity)

    @Query("DELETE FROM subscribed_podcasts WHERE id = :id")
    suspend fun deletePodcast(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM subscribed_podcasts WHERE id = :id)")
    fun isSubscribed(id: String): Flow<Boolean>
}
