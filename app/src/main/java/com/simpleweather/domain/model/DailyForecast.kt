package com.simpleweather.domain.model

data class DailyForecast(
    val date: String,
    val maxTempC: Double,
    val maxTempF: Double,
    val minTempC: Double,
    val minTempF: Double,
    val condition: String,
    val conditionIcon: String,
    val chanceOfRain: Int,
    val chanceOfSnow: Int,
    val avgHumidity: Int,
    val uv: Double
) {
    fun getMaxTemp(useCelsius: Boolean): String {
        return if (useCelsius) {
            "${maxTempC.toInt()}°"
        } else {
            "${maxTempF.toInt()}°"
        }
    }

    fun getMinTemp(useCelsius: Boolean): String {
        return if (useCelsius) {
            "${minTempC.toInt()}°"
        } else {
            "${minTempF.toInt()}°"
        }
    }

    fun getDayOfWeek(): String {
        // Simplified - in real app use proper date formatting
        return date
    }
}
