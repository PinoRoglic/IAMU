package com.simpleweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simpleweather.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecast WHERE cityId = :cityId ORDER BY date ASC")
    fun getForecastFlow(cityId: Int): Flow<List<ForecastEntity>>

    @Query("SELECT * FROM forecast WHERE cityId = :cityId ORDER BY date ASC")
    suspend fun getForecast(cityId: Int): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(forecasts: List<ForecastEntity>)

    @Query("DELETE FROM forecast WHERE cityId = :cityId")
    suspend fun delete(cityId: Int)

    @Query("DELETE FROM forecast")
    suspend fun deleteAll()
}
