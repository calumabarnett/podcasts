package com.podcasts.data.datasource.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<PodcastDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/podcasts.db"
    return Room.databaseBuilder<PodcastDatabase>(
        name = dbFilePath
    )
}
