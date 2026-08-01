package com.podcasts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcasts.domain.model.Episode
import com.podcasts.domain.model.Podcast
import com.podcasts.domain.repository.PodcastRepository
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    feedUrl: String,
    repository: PodcastRepository,
    onNavigateBack: () -> Unit,
    onPlayEpisode: (Episode) -> Unit
) {
    var feedData by remember { mutableStateOf<Pair<Podcast, List<Episode>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(feedUrl) {
        isLoading = true
        errorMessage = null
        try {
            feedData = repository.getPodcastFeed(feedUrl)
        } catch (e: Exception) {
            errorMessage = "Failed to load podcast feed"
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessage != null || feedData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = errorMessage ?: "Unknown error occurred", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            feedData = repository.getPodcastFeed(feedUrl)
                        } catch (e: Exception) {
                            errorMessage = "Failed to load podcast feed"
                        }
                        isLoading = false
                    }
                }) {
                    Text("Retry")
                }
            }
        }
        return
    }

    val (podcast, episodes) = feedData!!
    val isSubscribed by repository.isSubscribed(podcast.id).collectAsState(initial = false)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                PodcastHeaderSection(
                    podcast = podcast,
                    isSubscribed = isSubscribed,
                    onSubscribeToggle = {
                        coroutineScope.launch {
                            if (isSubscribed) {
                                repository.unsubscribeFromPodcast(podcast.id)
                            } else {
                                repository.subscribeToPodcast(podcast)
                            }
                        }
                    }
                )
            }

            item {
                Text(
                    text = "Episodes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(episodes) { episode ->
                EpisodeFeedItem(episode = episode, onPlay = onPlayEpisode)
            }
        }
    }
}

@Composable
fun PodcastHeaderSection(
    podcast: Podcast,
    isSubscribed: Boolean,
    onSubscribeToggle: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            PodcastPlaceholderImage(
                title = podcast.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = podcast.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = podcast.author,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Button(
                    onClick = onSubscribeToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (isSubscribed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (isSubscribed) "Subscribed" else "Subscribe",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = podcast.description,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun EpisodeFeedItem(
    episode: Episode,
    onPlay: (Episode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay(episode) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = episode.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = episode.description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatDate(episode.publishDate),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatDuration(episode.duration),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            IconButton(
                onClick = { onPlay(episode) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play"
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

fun formatDate(timestampMs: Long): String {
    if (timestampMs <= 0) return "Unknown date"
    // Convert to a simple day & month string across platforms
    val seconds = timestampMs / 1000
    val daysSince1970 = seconds / 86400
    // Simple rough year-month conversion
    var year = 1970
    var daysLeft = daysSince1970
    while (true) {
        val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val daysInYear = if (isLeap) 366 else 365
        if (daysLeft < daysInYear) break
        daysLeft -= daysInYear
        year++
    }

    val daysInMonths = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
        daysInMonths[1] = 29
    }

    var month = 0
    while (daysLeft >= daysInMonths[month]) {
        daysLeft -= daysInMonths[month]
        month++
    }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${monthNames[month]} ${(daysLeft + 1)}, $year"
}
