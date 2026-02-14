package com.simpleweather.domain.model

data class SavedCity(
    val id: Int,
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long
)
