package com.kashmir.thefilamentary.data.dao

import androidx.room.*
import com.kashmir.thefilamentary.data.entity.PrintLog
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the PrintLog entity
 */
@Dao
interface PrintLogDao {
    @Query("SELECT * FROM print_logs WHERE filamentId = :filamentId ORDER BY timestampLogged DESC")
    fun getPrintLogsForFilament(filamentId: Long): Flow<List<PrintLog>>
    
    @Query("SELECT * FROM print_logs WHERE id = :printLogId")
    fun getPrintLogById(printLogId: Long): Flow<PrintLog?>
    
    @Insert
    suspend fun insertPrintLog(printLog: PrintLog): Long
    
    @Update
    suspend fun updatePrintLog(printLog: PrintLog)
    
    @Delete
    suspend fun deletePrintLog(printLog: PrintLog)
}