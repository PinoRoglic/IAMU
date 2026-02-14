package com.simpleweather.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

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
                // Handle weather update event
                // You can add custom logic here
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "Boot completed, reinitializing weather sync")
                // WorkManager will automatically restart periodic work
            }
        }
    }
}
