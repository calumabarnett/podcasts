package com.podcasts.data.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PodcastDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("podcasts.db")
    return Room.databaseBuilder<PodcastDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
