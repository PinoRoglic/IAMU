package com.simpleweather.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.simpleweather.R
import com.simpleweather.SimpleWeatherApp
import com.simpleweather.ui.main.MainActivity

class WeatherReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WEATHER_UPDATE = "com.simpleweather.WEATHER_UPDATE"
        private const val TAG = "WeatherReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            ACTION_WEATHER_UPDATE -> {
                Log.d(TAG, "Weather update broadcast received")
                showTestNotification(context)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "Boot completed, reinitializing weather sync")
                // WorkManager will automatically restart periodic work
            }
        }
    }

    private fun showTestNotification(context: Context) {
        // Check if notifications are enabled in settings
        val preferencesManager = com.simpleweather.utils.PreferencesManager(context)
        if (!preferencesManager.notificationsEnabled) {
            Log.d(TAG, "Notifications are disabled in settings, skipping notification")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            SimpleWeatherApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText("Weather update broadcast received via ADB!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1002, notification)

        Log.d(TAG, "Test notification sent!")
    }
}
