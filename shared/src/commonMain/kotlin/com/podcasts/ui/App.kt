package com.podcasts.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.podcasts.domain.player.AudioPlayer
import com.podcasts.domain.repository.PodcastRepository
import org.koin.compose.koinInject

@Composable
fun App() {
    val repository: PodcastRepository = koinInject()
    val audioPlayer: AudioPlayer = koinInject()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isPlayerExpanded by remember { mutableStateOf(false) }

    val playbackState by audioPlayer.playbackState.collectAsState()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF1A73E8), // Classic Google Blue
            secondary = androidx.compose.ui.graphics.Color(0xFF5F6368),
            background = androidx.compose.ui.graphics.Color(0xFFF8F9FA), // Soft Google Off-White
            surface = androidx.compose.ui.graphics.Color.White,
            primaryContainer = androidx.compose.ui.graphics.Color(0xFFE8F0FE),
            onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1967D2)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Screen Content Router
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        is Screen.Home -> {
                            HomeScreen(
                                repository = repository,
                                onNavigateToSearch = { currentScreen = Screen.Search },
                                onNavigateToDetail = { feedUrl -> currentScreen = Screen.PodcastDetail(feedUrl) },
                                onPlayEpisode = { episode -> audioPlayer.play(episode) }
                            )
                        }
                        is Screen.Search -> {
                            SearchScreen(
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Home },
                                onNavigateToDetail = { feedUrl -> currentScreen = Screen.PodcastDetail(feedUrl) }
                            )
                        }
                        is Screen.PodcastDetail -> {
                            DetailScreen(
                                feedUrl = screen.feedUrl,
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Home },
                                onPlayEpisode = { episode -> audioPlayer.play(episode) }
                            )
                        }
                    }
                }

                // Docked Mini Player
                if (playbackState.currentEpisode != null && !isPlayerExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                    ) {
                        MiniPlayer(
                            playbackState = playbackState,
                            audioPlayer = audioPlayer,
                            onExpand = { isPlayerExpanded = true }
                        )
                    }
                }

                // Animated Full Screen Player Sheet
                AnimatedVisibility(
                    visible = isPlayerExpanded,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    FullPlayerSheet(
                        playbackState = playbackState,
                        audioPlayer = audioPlayer,
                        onCollapse = { isPlayerExpanded = false }
                    )
                }
            }
        }
    }
}
