package com.simpleweather.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simpleweather.data.repository.WeatherRepository
import com.simpleweather.domain.model.DailyForecast
import com.simpleweather.domain.model.WeatherInfo
import com.simpleweather.utils.PreferencesManager
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _weatherInfo = MutableLiveData<WeatherInfo?>()
    val weatherInfo: LiveData<WeatherInfo?> = _weatherInfo

    private val _forecast = MutableLiveData<List<DailyForecast>>()
    val forecast: LiveData<List<DailyForecast>> = _forecast

    val useCelsius: Boolean
        get() = preferencesManager.useCelsius

    val useKph: Boolean
        get() = preferencesManager.useKph

    fun loadWeatherDetails(cityId: Int) {
        viewModelScope.launch {
            repository.getWeatherFlow(cityId)
                .catch { e ->
                    // Handle error
                }
                .collect { weather ->
                    _weatherInfo.value = weather
                }
        }

        viewModelScope.launch {
            repository.getForecastFlow(cityId)
                .catch { e ->
                    // Handle error
                }
                .collect { forecasts ->
                    _forecast.value = forecasts
                }
        }
    }
}
