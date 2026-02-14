package com.simpleweather.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.simpleweather.R
import com.simpleweather.SimpleWeatherApp
import com.simpleweather.data.repository.WeatherRepository
import com.simpleweather.receiver.WeatherReceiver
import com.simpleweather.ui.main.MainActivity
import com.simpleweather.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WeatherSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: WeatherRepository by inject()
    private val preferencesManager: PreferencesManager by inject()

    companion object {
        private const val TAG = "WeatherSyncWorker"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting weather sync")

            // Check if auto-update is enabled
            if (!preferencesManager.autoUpdateEnabled) {
                Log.d(TAG, "Auto-update is disabled, skipping sync")
                return@withContext Result.success()
            }

            // Sync all saved cities
            val syncResult = repository.syncAllCities()

            if (syncResult.isSuccess) {
                Log.d(TAG, "Weather sync completed successfully")

                // Send broadcast
                val intent = Intent(WeatherReceiver.ACTION_WEATHER_UPDATE)
                applicationContext.sendBroadcast(intent)

                // Show notification if enabled
                if (preferencesManager.notificationsEnabled) {
                    showNotification()
                }

                Result.success()
            } else {
                Log.e(TAG, "Weather sync failed: ${syncResult.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during weather sync", e)
            Result.failure()
        }
    }

    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            SimpleWeatherApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.app_name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
