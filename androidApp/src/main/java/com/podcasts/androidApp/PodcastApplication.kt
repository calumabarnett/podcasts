package com.podcasts.androidApp

import android.app.Application
import com.podcasts.di.commonModule
import com.podcasts.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PodcastApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PodcastApplication)
            modules(commonModule, platformModule)
        }
    }
}
