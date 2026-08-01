package com.podcasts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcasts.domain.model.Podcast
import com.podcasts.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    repository: PodcastRepository,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Podcast>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(query) {
        isLoading = true
        results = try {
            repository.searchPodcasts(query)
        } catch (e: Exception) {
            emptyList()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search podcasts...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    )
                }
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLoading) 3.dp else 0.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { paddingValues ->
        if (results.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isBlank()) "Type to discover amazing podcasts" else "No podcasts found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(results) { podcast ->
                    SearchResultItem(
                        podcast = podcast,
                        repository = repository,
                        onClick = { onNavigateToDetail(podcast.feedUrl) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    podcast: Podcast,
    repository: PodcastRepository,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isSubscribed by repository.isSubscribed(podcast.id).collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PodcastPlaceholderImage(
            title = podcast.title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = podcast.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = podcast.author,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = podcast.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                coroutineScope.launch {
                    if (isSubscribed) {
                        repository.unsubscribeFromPodcast(podcast.id)
                    } else {
                        repository.subscribeToPodcast(podcast)
                    }
                }
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isSubscribed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isSubscribed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isSubscribed) Icons.Default.Check else Icons.Default.Add,
                contentDescription = if (isSubscribed) "Subscribed" else "Subscribe"
            )
        }
    }
}
