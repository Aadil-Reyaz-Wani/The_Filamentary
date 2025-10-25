package com.kashmir.thefilamentary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kashmir.thefilamentary.data.entity.Filament
import com.kashmir.thefilamentary.data.entity.PrintLog
import com.kashmir.thefilamentary.data.repository.FilamentRepository
import com.kashmir.thefilamentary.data.repository.PrintLogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FilamentDetailViewModel(
    private val filamentId: Long,
    private val filamentRepository: FilamentRepository,
    private val printLogRepository: PrintLogRepository
) : ViewModel() {
    
    val filament: StateFlow<Filament?> = filamentRepository.getFilamentById(filamentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    val printLogs: StateFlow<List<PrintLog>> = printLogRepository.getPrintLogsForFilament(filamentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun updateCurrentWeight(newWeight: Int) {
        viewModelScope.launch {
            filamentRepository.updateFilamentWeight(filamentId, newWeight)
        }
    }
    
    class Factory(
        private val filamentId: Long,
        private val filamentRepository: FilamentRepository,
        private val printLogRepository: PrintLogRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FilamentDetailViewModel::class.java)) {
                return FilamentDetailViewModel(filamentId, filamentRepository, printLogRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}