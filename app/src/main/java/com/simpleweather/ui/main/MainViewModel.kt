package com.simpleweather.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simpleweather.data.repository.WeatherRepository
import com.simpleweather.domain.model.DailyForecast
import com.simpleweather.domain.model.SavedCity
import com.simpleweather.domain.model.WeatherInfo
import com.simpleweather.utils.PreferencesManager
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _weatherInfo = MutableLiveData<WeatherInfo?>()
    val weatherInfo: LiveData<WeatherInfo?> = _weatherInfo

    private val _forecast = MutableLiveData<List<DailyForecast>>()
    val forecast: LiveData<List<DailyForecast>> = _forecast

    private val _savedCities = MutableLiveData<List<SavedCity>>()
    val savedCities: LiveData<List<SavedCity>> = _savedCities

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val useCelsius: Boolean
        get() = preferencesManager.useCelsius

    val useKph: Boolean
        get() = preferencesManager.useKph

    val notificationsEnabled: Boolean
        get() = preferencesManager.notificationsEnabled

    init {
        loadSavedCities()
    }

    fun loadSavedCities() {
        viewModelScope.launch {
            repository.getSavedCitiesFlow()
                .catch { e ->
                    Log.e(TAG, "Error loading saved cities", e)
                    _error.value = e.message
                }
                .collect { cities ->
                    _savedCities.value = cities
                }
        }
    }

    fun searchCity(cityName: String) {
        if (cityName.isBlank()) {
            _error.value = "Please enter a city name"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getWeather(cityName, fetchFromRemote = true)

            result.onSuccess { weatherInfo ->
                _weatherInfo.value = weatherInfo

                // Save the city
                repository.saveCity(
                    cityName = weatherInfo.cityName,
                    country = weatherInfo.country,
                    lat = weatherInfo.latitude,
                    lon = weatherInfo.longitude
                )

                // Load forecast
                loadForecast(weatherInfo.cityName)
            }

            result.onFailure { exception ->
                Log.e(TAG, "Error searching city", exception)
                _error.value = exception.message ?: "Failed to load weather data"
            }

            _isLoading.value = false
        }
    }

    fun loadWeather(cityName: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getWeather(cityName, fetchFromRemote = true)

            result.onSuccess { weatherInfo ->
                _weatherInfo.value = weatherInfo
                loadForecast(cityName)
            }

            result.onFailure { exception ->
                Log.e(TAG, "Error loading weather", exception)
                _error.value = exception.message ?: "Failed to load weather data"
            }

            _isLoading.value = false
        }
    }

    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getWeatherByCoordinates(lat, lon)

            result.onSuccess { weatherInfo ->
                _weatherInfo.value = weatherInfo

                // Save the city
                repository.saveCity(
                    cityName = weatherInfo.cityName,
                    country = weatherInfo.country,
                    lat = weatherInfo.latitude,
                    lon = weatherInfo.longitude
                )

                loadForecast(weatherInfo.cityName)
            }

            result.onFailure { exception ->
                Log.e(TAG, "Error loading weather by coordinates", exception)
                _error.value = exception.message ?: "Failed to load weather data"
            }

            _isLoading.value = false
        }
    }

    private fun loadForecast(cityName: String) {
        viewModelScope.launch {
            val result = repository.getForecast(cityName, fetchFromRemote = true)

            result.onSuccess { forecasts ->
                Log.d(TAG, "Loaded ${forecasts.size} forecast days for $cityName")
                _forecast.value = forecasts
            }

            result.onFailure { exception ->
                Log.e(TAG, "Error loading forecast", exception)
            }
        }
    }

    fun refreshWeather() {
        val currentWeather = _weatherInfo.value
        if (currentWeather != null) {
            _isRefreshing.value = true

            viewModelScope.launch {
                val result = repository.refreshWeather(currentWeather.cityId)

                result.onSuccess { weatherInfo ->
                    _weatherInfo.value = weatherInfo
                    loadForecast(weatherInfo.cityName)
                }

                result.onFailure { exception ->
                    Log.e(TAG, "Error refreshing weather", exception)
                    _error.value = exception.message
                }

                _isRefreshing.value = false
            }
        }
    }

    fun deleteCity(cityId: Int) {
        viewModelScope.launch {
            val result = repository.deleteCity(cityId)

            result.onSuccess {
                Log.d(TAG, "City deleted successfully")
                // Clear current weather if it's the deleted city
                if (_weatherInfo.value?.cityId == cityId) {
                    _weatherInfo.value = null
                    _forecast.value = emptyList()
                }
            }

            result.onFailure { exception ->
                Log.e(TAG, "Error deleting city", exception)
                _error.value = exception.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
