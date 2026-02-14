package com.simpleweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityId: Int,
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
)
