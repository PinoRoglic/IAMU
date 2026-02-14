package com.simpleweather

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.simpleweather.di.appModule
import com.simpleweather.worker.WeatherSyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class SimpleWeatherApp : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "weather_updates"
        const val WEATHER_SYNC_WORK_NAME = "weather_sync_work"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin DI
        startKoin {
            androidLogger()
            androidContext(this@SimpleWeatherApp)
            modules(appModule)
        }

        // Create notification channel
        createNotificationChannel()

        // Schedule periodic weather updates
        scheduleWeatherSync()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleWeatherSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherSyncRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
            3, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WEATHER_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            weatherSyncRequest
        )
    }
}
