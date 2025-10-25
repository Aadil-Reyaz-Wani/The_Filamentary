package com.kashmir.thefilamentary.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kashmir.thefilamentary.data.entity.PrintLog
import com.kashmir.thefilamentary.data.repository.PrintLogRepository
import com.kashmir.thefilamentary.util.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class PrintLogEditViewModel(
    private val printLogRepository: PrintLogRepository,
    private val filamentId: Long,
    private val printLogId: Long = 0
) : ViewModel() {
    
    private val _printLogState = MutableStateFlow<PrintLog?>(null)
    val printLogState: StateFlow<PrintLog?> = _printLogState.asStateFlow()
    
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()
    
    init {
        if (printLogId > 0) {
            viewModelScope.launch {
                printLogRepository.getPrintLogById(printLogId)
                    .collect { printLog ->
                        _printLogState.value = printLog
                    }
            }
        }
    }
    
    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }
    
    fun savePrintLog(
        context: Context,
        nozzleTempC: Int,
        bedTempC: Int,
        printSpeedMms: Int?,
        outcomeRating: Int,
        notes: String?,
        filamentUsedGrams: Int?
    ) {
        viewModelScope.launch {
            // Process image if selected
            val imagePath = _selectedImageUri.value?.let { uri ->
                ImageUtils.saveCompressedImage(context, uri)
            } ?: _printLogState.value?.imagePath
            
            val printLog = if (printLogId > 0) {
                _printLogState.value?.copy(
                    nozzleTempC = nozzleTempC,
                    bedTempC = bedTempC,
                    printSpeedMms = printSpeedMms,
                    outcomeRating = outcomeRating,
                    notes = notes,
                    filamentUsedGrams = filamentUsedGrams,
                    imagePath = imagePath
                ) ?: return@launch
            } else {
                PrintLog(
                    filamentId = filamentId,
                    nozzleTempC = nozzleTempC,
                    bedTempC = bedTempC,
                    printSpeedMms = printSpeedMms,
                    outcomeRating = outcomeRating,
                    notes = notes,
                    filamentUsedGrams = filamentUsedGrams,
                    imagePath = imagePath
                )
            }
            
            if (printLogId > 0) {
                printLogRepository.updatePrintLog(printLog)
            } else {
                printLogRepository.insertPrintLog(printLog)
            }
        }
    }
    
    class Factory(
        private val repository: PrintLogRepository,
        private val filamentId: Long,
        private val printLogId: Long = 0
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PrintLogEditViewModel::class.java)) {
                return PrintLogEditViewModel(repository, filamentId, printLogId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}