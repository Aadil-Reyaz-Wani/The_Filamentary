package com.kashmir.thefilamentary.data.repository

import com.kashmir.thefilamentary.data.dao.PrintLogDao
import com.kashmir.thefilamentary.data.entity.PrintLog
import kotlinx.coroutines.flow.Flow

class PrintLogRepository(private val printLogDao: PrintLogDao) {
    fun getPrintLogsForFilament(filamentId: Long): Flow<List<PrintLog>> = 
        printLogDao.getPrintLogsForFilament(filamentId)
    
    fun getPrintLogById(printLogId: Long): Flow<PrintLog?> = 
        printLogDao.getPrintLogById(printLogId)
    
    suspend fun insertPrintLog(printLog: PrintLog): Long = 
        printLogDao.insertPrintLog(printLog)
    
    suspend fun updatePrintLog(printLog: PrintLog) = 
        printLogDao.updatePrintLog(printLog)
    
    suspend fun deletePrintLog(printLog: PrintLog) = 
        printLogDao.deletePrintLog(printLog)
}