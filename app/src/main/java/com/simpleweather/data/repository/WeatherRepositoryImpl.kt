package com.simpleweather.data.repository

import android.util.Log
import com.simpleweather.BuildConfig
import com.simpleweather.data.local.dao.CityDao
import com.simpleweather.data.local.dao.ForecastDao
import com.simpleweather.data.local.dao.WeatherDao
import com.simpleweather.data.local.entities.CityEntity
import com.simpleweather.data.local.entities.ForecastEntity
import com.simpleweather.data.local.entities.WeatherEntity
import com.simpleweather.data.remote.WeatherApiService
import com.simpleweather.data.remote.dto.WeatherApiResponse
import com.simpleweather.domain.model.DailyForecast
import com.simpleweather.domain.model.SavedCity
import com.simpleweather.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
    private val forecastDao: ForecastDao
) : WeatherRepository {

    companion object {
        private const val TAG = "WeatherRepository"
    }

    override suspend fun getWeather(cityName: String, fetchFromRemote: Boolean): Result<WeatherInfo> {
        return try {
            if (fetchFromRemote) {
                // Fetch from API
                val response = weatherApiService.getWeatherForecast(
                    apiKey = BuildConfig.WEATHER_API_KEY,
                    city = cityName,
                    days = 7
                )

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val weatherEntity = mapToWeatherEntity(apiResponse)

                    // Save to database
                    weatherDao.insert(weatherEntity)

                    // Save forecast if available
                    apiResponse.forecast?.let { forecast ->
                        val forecastEntities = forecast.forecastDay.map { day ->
                            ForecastEntity(
                                cityId = weatherEntity.cityId,
                                date = day.date,
                                maxTempC = day.day.maxTempC,
                                maxTempF = day.day.maxTempF,
                                minTempC = day.day.minTempC,
                                minTempF = day.day.minTempF,
                                condition = day.day.condition.text,
                                conditionIcon = day.day.condition.icon,
                                chanceOfRain = day.day.dailyChanceOfRain,
                                chanceOfSnow = day.day.dailyChanceOfSnow,
                                avgHumidity = day.day.avgHumidity,
                                uv = day.day.uv
                            )
                        }
                        forecastDao.insertAll(forecastEntities)
                    }

                    Result.success(mapToWeatherInfo(weatherEntity))
                } else {
                    Result.failure(Exception("Failed to fetch weather: ${response.message()}"))
                }
            } else {
                // Try to get from database
                val city = cityDao.getAllCities().find {
                    it.cityName.equals(cityName, ignoreCase = true)
                }

                if (city != null) {
                    val weather = weatherDao.getWeather(city.id)
                    if (weather != null) {
                        Result.success(mapToWeatherInfo(weather))
                    } else {
                        // Not in DB, fetch from API
                        getWeather(cityName, true)
                    }
                } else {
                    // City not found, fetch from API
                    getWeather(cityName, true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weather", e)
            Result.failure(e)
        }
    }

    override suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Result<WeatherInfo> {
        return try {
            val response = weatherApiService.getWeatherByCoordinates(
                apiKey = BuildConfig.WEATHER_API_KEY,
                coordinates = "$lat,$lon",
                days = 7
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                val weatherEntity = mapToWeatherEntity(apiResponse)

                weatherDao.insert(weatherEntity)

                Result.success(mapToWeatherInfo(weatherEntity))
            } else {
                Result.failure(Exception("Failed to fetch weather: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weather by coordinates", e)
            Result.failure(e)
        }
    }

    override fun getWeatherFlow(cityId: Int): Flow<WeatherInfo?> {
        return weatherDao.getWeatherFlow(cityId).map { entity ->
            entity?.let { mapToWeatherInfo(it) }
        }
    }

    override suspend fun refreshWeather(cityId: Int): Result<WeatherInfo> {
        return try {
            val city = cityDao.getCity(cityId)
            if (city != null) {
                getWeather(city.cityName, true)
            } else {
                Result.failure(Exception("City not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing weather", e)
            Result.failure(e)
        }
    }

    override suspend fun getForecast(cityName: String, fetchFromRemote: Boolean): Result<List<DailyForecast>> {
        return try {
            if (fetchFromRemote) {
                // This is already fetched with getWeather, just return from DB
                val city = cityDao.getAllCities().find {
                    it.cityName.equals(cityName, ignoreCase = true)
                }

                if (city != null) {
                    val forecasts = forecastDao.getForecast(city.id)
                    Result.success(forecasts.map { mapToForecast(it) })
                } else {
                    Result.success(emptyList())
                }
            } else {
                val city = cityDao.getAllCities().find {
                    it.cityName.equals(cityName, ignoreCase = true)
                }

                if (city != null) {
                    val forecasts = forecastDao.getForecast(city.id)
                    Result.success(forecasts.map { mapToForecast(it) })
                } else {
                    Result.success(emptyList())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting forecast", e)
            Result.failure(e)
        }
    }

    override fun getForecastFlow(cityId: Int): Flow<List<DailyForecast>> {
        return forecastDao.getForecastFlow(cityId).map { entities ->
            entities.map { mapToForecast(it) }
        }
    }

    override suspend fun saveCity(cityName: String, country: String, lat: Double, lon: Double): Result<SavedCity> {
        return try {
            // Check if city already exists
            val existingCity = cityDao.getCityByName(cityName, country)
            if (existingCity != null) {
                return Result.success(mapToSavedCity(existingCity))
            }

            val cityEntity = CityEntity(
                cityName = cityName,
                country = country,
                latitude = lat,
                longitude = lon
            )

            val id = cityDao.insert(cityEntity).toInt()
            Result.success(mapToSavedCity(cityEntity.copy(id = id)))
        } catch (e: Exception) {
            Log.e(TAG, "Error saving city", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCity(cityId: Int): Result<Unit> {
        return try {
            cityDao.delete(cityId)
            weatherDao.delete(cityId)
            forecastDao.delete(cityId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting city", e)
            Result.failure(e)
        }
    }

    override fun getSavedCitiesFlow(): Flow<List<SavedCity>> {
        return cityDao.getAllCitiesFlow().map { entities ->
            entities.map { mapToSavedCity(it) }
        }
    }

    override suspend fun getSavedCities(): List<SavedCity> {
        return cityDao.getAllCities().map { mapToSavedCity(it) }
    }

    override suspend fun syncAllCities(): Result<Unit> {
        return try {
            val cities = cityDao.getAllCities()
            cities.forEach { city ->
                try {
                    getWeather(city.cityName, true)
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing city: ${city.cityName}", e)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing all cities", e)
            Result.failure(e)
        }
    }

    // Mapping functions
    private fun mapToWeatherEntity(apiResponse: WeatherApiResponse): WeatherEntity {
        return WeatherEntity(
            cityId = apiResponse.location.name.hashCode(),
            cityName = apiResponse.location.name,
            country = apiResponse.location.country,
            tempC = apiResponse.current.tempC,
            tempF = apiResponse.current.tempF,
            feelsLikeC = apiResponse.current.feelslikeC,
            feelsLikeF = apiResponse.current.feelslikeF,
            condition = apiResponse.current.condition.text,
            conditionIcon = apiResponse.current.condition.icon,
            humidity = apiResponse.current.humidity,
            windKph = apiResponse.current.windKph,
            windMph = apiResponse.current.windMph,
            windDir = apiResponse.current.windDir,
            pressureMb = apiResponse.current.pressureMb,
            visibilityKm = apiResponse.current.visKm,
            uv = apiResponse.current.uv,
            lastUpdated = apiResponse.current.lastUpdatedEpoch,
            latitude = apiResponse.location.lat,
            longitude = apiResponse.location.lon
        )
    }

    private fun mapToWeatherInfo(entity: WeatherEntity): WeatherInfo {
        return WeatherInfo(
            cityId = entity.cityId,
            cityName = entity.cityName,
            country = entity.country,
            tempC = entity.tempC,
            tempF = entity.tempF,
            feelsLikeC = entity.feelsLikeC,
            feelsLikeF = entity.feelsLikeF,
            condition = entity.condition,
            conditionIcon = entity.conditionIcon,
            humidity = entity.humidity,
            windKph = entity.windKph,
            windMph = entity.windMph,
            windDir = entity.windDir,
            pressureMb = entity.pressureMb,
            visibilityKm = entity.visibilityKm,
            uv = entity.uv,
            lastUpdated = entity.lastUpdated,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }

    private fun mapToForecast(entity: ForecastEntity): DailyForecast {
        return DailyForecast(
            date = entity.date,
            maxTempC = entity.maxTempC,
            maxTempF = entity.maxTempF,
            minTempC = entity.minTempC,
            minTempF = entity.minTempF,
            condition = entity.condition,
            conditionIcon = entity.conditionIcon,
            chanceOfRain = entity.chanceOfRain,
            chanceOfSnow = entity.chanceOfSnow,
            avgHumidity = entity.avgHumidity,
            uv = entity.uv
        )
    }

    private fun mapToSavedCity(entity: CityEntity): SavedCity {
        return SavedCity(
            id = entity.id,
            cityName = entity.cityName,
            country = entity.country,
            latitude = entity.latitude,
            longitude = entity.longitude,
            addedAt = entity.addedAt
        )
    }
}
