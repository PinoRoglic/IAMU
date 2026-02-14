package com.simpleweather.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
        private const val KEY_AUTO_UPDATE = "auto_update"
        private const val KEY_UPDATE_INTERVAL = "update_interval"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LAST_LOCATION_LAT = "last_location_lat"
        private const val KEY_LAST_LOCATION_LON = "last_location_lon"

        const val CELSIUS = "celsius"
        const val FAHRENHEIT = "fahrenheit"
        const val KPH = "kph"
        const val MPH = "mph"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Temperature Unit
    var temperatureUnit: String
        get() = prefs.getString(KEY_TEMPERATURE_UNIT, CELSIUS) ?: CELSIUS
        set(value) = prefs.edit { putString(KEY_TEMPERATURE_UNIT, value) }

    val useCelsius: Boolean
        get() = temperatureUnit == CELSIUS

    // Wind Speed Unit
    var windSpeedUnit: String
        get() = prefs.getString(KEY_WIND_SPEED_UNIT, KPH) ?: KPH
        set(value) = prefs.edit { putString(KEY_WIND_SPEED_UNIT, value) }

    val useKph: Boolean
        get() = windSpeedUnit == KPH

    // Auto Update
    var autoUpdateEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_UPDATE, true)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_UPDATE, value) }

    // Update Interval (in hours)
    var updateInterval: Int
        get() = prefs.getInt(KEY_UPDATE_INTERVAL, 3)
        set(value) = prefs.edit { putInt(KEY_UPDATE_INTERVAL, value) }

    // Notifications
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    // Last Location
    var lastLocationLat: Double
        get() = prefs.getString(KEY_LAST_LOCATION_LAT, "0.0")?.toDoubleOrNull() ?: 0.0
        set(value) = prefs.edit { putString(KEY_LAST_LOCATION_LAT, value.toString()) }

    var lastLocationLon: Double
        get() = prefs.getString(KEY_LAST_LOCATION_LON, "0.0")?.toDoubleOrNull() ?: 0.0
        set(value) = prefs.edit { putString(KEY_LAST_LOCATION_LON, value.toString()) }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
