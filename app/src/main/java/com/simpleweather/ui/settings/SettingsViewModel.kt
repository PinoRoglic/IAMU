package com.simpleweather.ui.settings

import androidx.lifecycle.ViewModel
import com.simpleweather.utils.PreferencesManager

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var temperatureUnit: String
        get() = preferencesManager.temperatureUnit
        set(value) {
            preferencesManager.temperatureUnit = value
        }

    var windSpeedUnit: String
        get() = preferencesManager.windSpeedUnit
        set(value) {
            preferencesManager.windSpeedUnit = value
        }

    var autoUpdateEnabled: Boolean
        get() = preferencesManager.autoUpdateEnabled
        set(value) {
            preferencesManager.autoUpdateEnabled = value
        }

    var updateInterval: Int
        get() = preferencesManager.updateInterval
        set(value) {
            preferencesManager.updateInterval = value
        }

    var notificationsEnabled: Boolean
        get() = preferencesManager.notificationsEnabled
        set(value) {
            preferencesManager.notificationsEnabled = value
        }
}
