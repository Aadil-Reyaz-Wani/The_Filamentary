package com.kashmir.thefilamentary.data.dao

import androidx.room.*
import com.kashmir.thefilamentary.data.entity.Filament
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Filament entity
 */
@Dao
interface FilamentDao {
    @Query("SELECT * FROM filaments ORDER BY timestampAdded DESC")
    fun getAllFilaments(): Flow<List<Filament>>
    
    @Query("SELECT * FROM filaments WHERE id = :filamentId")
    fun getFilamentById(filamentId: Long): Flow<Filament?>
    
    @Insert
    suspend fun insertFilament(filament: Filament): Long
    
    @Update
    suspend fun updateFilament(filament: Filament)
    
    @Delete
    suspend fun deleteFilament(filament: Filament)
    
    @Query("UPDATE filaments SET currentWeightGrams = :newWeight WHERE id = :filamentId")
    suspend fun updateFilamentWeight(filamentId: Long, newWeight: Int)
    
    @Query("SELECT COUNT(*) FROM filaments")
    fun getFilamentCount(): Flow<Int>
}