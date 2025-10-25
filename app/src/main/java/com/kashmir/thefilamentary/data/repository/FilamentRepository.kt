package com.kashmir.thefilamentary.data.repository

import com.kashmir.thefilamentary.data.dao.FilamentDao
import com.kashmir.thefilamentary.data.entity.Filament
import kotlinx.coroutines.flow.Flow

class FilamentRepository(private val filamentDao: FilamentDao) {
    fun getAllFilaments(): Flow<List<Filament>> = filamentDao.getAllFilaments()
    
    fun getFilamentById(filamentId: Long): Flow<Filament?> = filamentDao.getFilamentById(filamentId)
    
    suspend fun insertFilament(filament: Filament): Long = filamentDao.insertFilament(filament)
    
    suspend fun updateFilament(filament: Filament) = filamentDao.updateFilament(filament)
    
    suspend fun deleteFilament(filament: Filament) = filamentDao.deleteFilament(filament)
    
    suspend fun updateFilamentWeight(filamentId: Long, newWeight: Int) = 
        filamentDao.updateFilamentWeight(filamentId, newWeight)
    
    fun getFilamentCount(): Flow<Int> = filamentDao.getFilamentCount()
}