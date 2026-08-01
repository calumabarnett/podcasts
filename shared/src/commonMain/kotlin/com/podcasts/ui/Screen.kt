package com.podcasts.ui

sealed interface Screen {
    data object Home : Screen
    data object Search : Screen
    data class PodcastDetail(val feedUrl: String) : Screen
}
