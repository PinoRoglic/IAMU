package com.simpleweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simpleweather.data.local.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE cityId = :cityId")
    suspend fun getWeather(cityId: Int): WeatherEntity?

    @Query("SELECT * FROM weather WHERE cityId = :cityId")
    fun getWeatherFlow(cityId: Int): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather")
    fun getAllWeatherFlow(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weatherList: List<WeatherEntity>)

    @Query("DELETE FROM weather WHERE cityId = :cityId")
    suspend fun delete(cityId: Int)

    @Query("DELETE FROM weather")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM weather")
    suspend fun getCount(): Int
}
