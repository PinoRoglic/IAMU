package com.simpleweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = System.currentTimeMillis()
)
