package com.podcasts.utils

actual fun getCurrentTimeSeconds(): Long {
    return System.currentTimeMillis() / 1000
}
