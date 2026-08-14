package com.podcasts.data.datasource.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeProgressDao {
    @Query("SELECT * FROM episode_progress WHERE playbackPosition > 0 AND isCompleted = 0 ORDER BY lastUpdated DESC")
    fun getEpisodesInProgress(): Flow<List<EpisodeProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: EpisodeProgressEntity)

    @Query("SELECT playbackPosition FROM episode_progress WHERE id = :episodeId")
    fun getPlaybackPosition(episodeId: String): Flow<Long?>

    @Query("SELECT * FROM episode_progress WHERE id = :episodeId")
    fun getEpisodeProgress(episodeId: String): Flow<EpisodeProgressEntity?>
}
