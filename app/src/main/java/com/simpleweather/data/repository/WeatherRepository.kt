package com.simpleweather.data.repository

import com.simpleweather.domain.model.DailyForecast
import com.simpleweather.domain.model.SavedCity
import com.simpleweather.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    // Weather operations
    suspend fun getWeather(cityName: String, fetchFromRemote: Boolean = true): Result<WeatherInfo>
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Result<WeatherInfo>
    fun getWeatherFlow(cityId: Int): Flow<WeatherInfo?>
    suspend fun refreshWeather(cityId: Int): Result<WeatherInfo>

    // Forecast operations
    suspend fun getForecast(cityName: String, fetchFromRemote: Boolean = true): Result<List<DailyForecast>>
    fun getForecastFlow(cityId: Int): Flow<List<DailyForecast>>

    // City operations
    suspend fun saveCity(cityName: String, country: String, lat: Double, lon: Double): Result<SavedCity>
    suspend fun deleteCity(cityId: Int): Result<Unit>
    fun getSavedCitiesFlow(): Flow<List<SavedCity>>
    suspend fun getSavedCities(): List<SavedCity>

    // Sync operations
    suspend fun syncAllCities(): Result<Unit>
}
