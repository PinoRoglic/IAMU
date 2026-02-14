package com.simpleweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
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
)
