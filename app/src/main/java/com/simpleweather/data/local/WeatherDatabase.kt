package com.simpleweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simpleweather.data.local.dao.CityDao
import com.simpleweather.data.local.dao.ForecastDao
import com.simpleweather.data.local.dao.WeatherDao
import com.simpleweather.data.local.entities.CityEntity
import com.simpleweather.data.local.entities.ForecastEntity
import com.simpleweather.data.local.entities.WeatherEntity

@Database(
    entities = [
        WeatherEntity::class,
        CityEntity::class,
        ForecastEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun cityDao(): CityDao
    abstract fun forecastDao(): ForecastDao

    companion object {
        const val DATABASE_NAME = "simple_weather.db"
    }
}
