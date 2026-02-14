package com.simpleweather.domain.model

data class WeatherInfo(
    val cityId: Int,
    val cityName: String,
    val country: String,
    val tempC: Double,
    val tempF: Double,
    val feelsLikeC: Double,
    val feelsLikeF: Double,
    val condition: String,
    val conditionIcon: String,
    val humidity: Int,
    val windKph: Double,
    val windMph: Double,
    val windDir: String,
    val pressureMb: Double,
    val visibilityKm: Double,
    val uv: Double,
    val lastUpdated: Long,
    val latitude: Double,
    val longitude: Double
) {
    fun getDisplayTemp(useCelsius: Boolean): String {
        return if (useCelsius) {
            "${tempC.toInt()}°C"
        } else {
            "${tempF.toInt()}°F"
        }
    }

    fun getFeelsLikeTemp(useCelsius: Boolean): String {
        return if (useCelsius) {
            "${feelsLikeC.toInt()}°C"
        } else {
            "${feelsLikeF.toInt()}°F"
        }
    }

    fun getWindSpeed(useKph: Boolean): String {
        return if (useKph) {
            "${windKph.toInt()} km/h"
        } else {
            "${windMph.toInt()} mph"
        }
    }
}
