package com.simpleweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simpleweather.data.local.entities.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM cities ORDER BY addedAt DESC")
    fun getAllCitiesFlow(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities ORDER BY addedAt DESC")
    suspend fun getAllCities(): List<CityEntity>

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getCity(id: Int): CityEntity?

    @Query("SELECT * FROM cities WHERE cityName = :cityName AND country = :country LIMIT 1")
    suspend fun getCityByName(cityName: String, country: String): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity): Long

    @Query("DELETE FROM cities WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM cities")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun getCount(): Int
}
