package com.simpleweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    @SerializedName("location")
    val location: LocationDto,
    @SerializedName("current")
    val current: CurrentWeatherDto,
    @SerializedName("forecast")
    val forecast: ForecastDto? = null
)

data class LocationDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("localtime_epoch")
    val localtimeEpoch: Long,
    @SerializedName("localtime")
    val localtime: String
)

data class CurrentWeatherDto(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("temp_f")
    val tempF: Double,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("feelslike_f")
    val feelslikeF: Double,
    @SerializedName("condition")
    val condition: ConditionDto,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("wind_mph")
    val windMph: Double,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("vis_km")
    val visKm: Double,
    @SerializedName("uv")
    val uv: Double,
    @SerializedName("last_updated_epoch")
    val lastUpdatedEpoch: Long
)

data class ConditionDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("code")
    val code: Int
)

data class ForecastDto(
    @SerializedName("forecastday")
    val forecastDay: List<ForecastDayDto>
)

data class ForecastDayDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("date_epoch")
    val dateEpoch: Long,
    @SerializedName("day")
    val day: DayDto,
    @SerializedName("astro")
    val astro: AstroDto
)

data class DayDto(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("maxtemp_f")
    val maxTempF: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("mintemp_f")
    val minTempF: Double,
    @SerializedName("avgtemp_c")
    val avgTempC: Double,
    @SerializedName("avgtemp_f")
    val avgTempF: Double,
    @SerializedName("condition")
    val condition: ConditionDto,
    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,
    @SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int,
    @SerializedName("avghumidity")
    val avgHumidity: Int,
    @SerializedName("uv")
    val uv: Double
)

data class AstroDto(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("moonrise")
    val moonrise: String,
    @SerializedName("moonset")
    val moonset: String
)
