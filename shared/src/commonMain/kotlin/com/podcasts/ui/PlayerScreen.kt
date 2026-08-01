package com.podcasts.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcasts.domain.model.PlaybackState
import com.podcasts.domain.player.AudioPlayer

@Composable
fun MiniPlayer(
    playbackState: PlaybackState,
    audioPlayer: AudioPlayer,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val episode = playbackState.currentEpisode ?: return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f))
            .clickable(onClick = onExpand)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PodcastPlaceholderImage(
            title = episode.podcastTitle,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = episode.podcastTitle,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (playbackState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(2.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = {
                        if (playbackState.isPlaying) audioPlayer.pause() else audioPlayer.resume()
                    }
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            IconButton(onClick = { audioPlayer.skipForward() }) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Skip Forward 30s",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun FullPlayerSheet(
    playbackState: PlaybackState,
    audioPlayer: AudioPlayer,
    onCollapse: () -> Unit
) {
    val episode = playbackState.currentEpisode ?: return
    var showSpeedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Collapse Player")
                }
                Text(
                    text = "Now Playing",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) // Equalizes top layout balance
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            PodcastPlaceholderImage(
                title = episode.podcastTitle,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = episode.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.podcastTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                var sliderPosition by remember(playbackState.currentPosition) {
                    mutableStateOf(playbackState.currentPosition.toFloat())
                }
                var isUserDragging by remember { mutableStateOf(false) }

                Slider(
                    value = if (isUserDragging) sliderPosition else playbackState.currentPosition.toFloat(),
                    onValueChange = {
                        isUserDragging = true
                        sliderPosition = it
                    },
                    onValueChangeFinished = {
                        isUserDragging = false
                        audioPlayer.seekTo(sliderPosition.toLong())
                    },
                    valueRange = 0f..playbackState.duration.coerceAtLeast(1f).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(playbackState.currentPosition),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "-" + formatTime(maxOf(0L, playbackState.duration - playbackState.currentPosition)),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { audioPlayer.skipBackward() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Skip Backward 10s",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                if (playbackState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                if (playbackState.isPlaying) audioPlayer.pause() else audioPlayer.resume()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = { audioPlayer.skipForward() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Skip Forward 30s",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSpeedDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Playback Speed",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${playbackState.speed}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Playback Speed", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val speeds = listOf(0.5f, 0.8f, 1.0f, 1.25f, 1.5f, 2.0f)
                    speeds.forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    audioPlayer.setPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${speed}x",
                                fontSize = 15.sp,
                                fontWeight = if (playbackState.speed == speed) FontWeight.Bold else FontWeight.Normal,
                                color = if (playbackState.speed == speed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (playbackState.speed == speed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}
