package com.podcasts.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.PlaybackState
import com.podcasts.domain.player.AudioPlayer
import com.podcasts.domain.repository.PodcastRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull

class AndroidAudioPlayer(
    context: Context,
    private val repository: PodcastRepository
) : AudioPlayer {

    private val appContext = context.applicationContext
    private val exoPlayer = ExoPlayer.Builder(appContext).build()

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null
    private var lastSavedPosition = 0L

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateState()
                if (isPlaying) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                    saveProgressToRepository()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                updateState()
                if (state == Player.STATE_ENDED) {
                    markEpisodeCompleted()
                }
            }
        })
    }

    override fun play(episode: Episode) {
        coroutineScope.launch {
            repository.startEpisodePlayback(episode)

            val initialPosition = repository.getEpisodePlaybackProgress(episode.id).firstOrNull() ?: episode.playbackPosition

            _playbackState.value = PlaybackState(
                currentEpisode = episode,
                isPlaying = true,
                isLoading = true,
                currentPosition = initialPosition,
                duration = episode.duration * 1000
            )

            exoPlayer.setMediaItem(MediaItem.fromUri(episode.audioUrl))
            exoPlayer.prepare()
            exoPlayer.seekTo(initialPosition)
            exoPlayer.setPlaybackSpeed(_playbackState.value.speed)
            exoPlayer.play()
        }
    }

    override fun resume() {
        if (exoPlayer.playbackState != Player.STATE_IDLE) {
            exoPlayer.play()
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        updateState()
        saveProgressToRepository()
    }

    override fun skipForward() {
        val current = exoPlayer.currentPosition
        val duration = exoPlayer.duration
        val target = if (duration > 0) minOf(current + 30000L, duration) else current + 30000L
        seekTo(target)
    }

    override fun skipBackward() {
        val target = maxOf(0L, exoPlayer.currentPosition - 10000L)
        seekTo(target)
    }

    override fun setPlaybackSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(speed = speed)
        exoPlayer.setPlaybackSpeed(speed)
    }

    override fun release() {
        stopProgressTracking()
        saveProgressToRepository()
        exoPlayer.release()
        coroutineScope.cancel()
    }

    private fun updateState() {
        val currentEpisode = _playbackState.value.currentEpisode ?: return
        val isPlaying = exoPlayer.isPlaying
        val isLoading = exoPlayer.playbackState == Player.STATE_BUFFERING
        val currentPosition = exoPlayer.currentPosition
        val duration = if (exoPlayer.duration > 0) exoPlayer.duration else currentEpisode.duration * 1000

        _playbackState.value = _playbackState.value.copy(
            isPlaying = isPlaying,
            isLoading = isLoading,
            currentPosition = currentPosition,
            duration = duration
        )
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            var counter = 0
            while (isActive) {
                delay(500)
                updateState()

                counter++
                if (counter >= 10) { // Every 5 seconds
                    saveProgressToRepository()
                    counter = 0
                }
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun saveProgressToRepository() {
        val state = _playbackState.value
        val episode = state.currentEpisode ?: return
        val position = state.currentPosition

        if (position != lastSavedPosition && position > 0) {
            lastSavedPosition = position
            coroutineScope.launch {
                repository.updateEpisodePlaybackProgress(episode.id, position, false)
            }
        }
    }

    private fun markEpisodeCompleted() {
        val episode = _playbackState.value.currentEpisode ?: return
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentPosition = 0
        )
        coroutineScope.launch {
            repository.updateEpisodePlaybackProgress(episode.id, 0L, true)
        }
    }
}
