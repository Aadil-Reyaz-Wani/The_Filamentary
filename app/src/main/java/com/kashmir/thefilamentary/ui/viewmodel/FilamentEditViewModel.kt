package com.kashmir.thefilamentary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kashmir.thefilamentary.data.entity.Filament
import com.kashmir.thefilamentary.data.repository.FilamentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FilamentEditViewModel(
    private val filamentRepository: FilamentRepository,
    private val filamentId: Long = 0
) : ViewModel() {
    
    private val _filamentState = MutableStateFlow<Filament?>(null)
    val filamentState: StateFlow<Filament?> = _filamentState.asStateFlow()
    
    init {
        if (filamentId > 0) {
            viewModelScope.launch {
                filamentRepository.getFilamentById(filamentId)
                    .collect { filament ->
                        _filamentState.value = filament
                    }
            }
        }
    }
    
    fun saveFilament(
        brand: String,
        material: String,
        color: String,
        purchasePrice: Double?,
        initialWeightGrams: Int,
        currentWeightGrams: Int
    ) {
        viewModelScope.launch {
            val filament = if (filamentId > 0) {
                _filamentState.value?.copy(
                    brand = brand,
                    material = material,
                    color = color,
                    purchasePrice = purchasePrice,
                    initialWeightGrams = initialWeightGrams,
                    currentWeightGrams = currentWeightGrams
                ) ?: return@launch
            } else {
                Filament(
                    brand = brand,
                    material = material,
                    color = color,
                    purchasePrice = purchasePrice,
                    initialWeightGrams = initialWeightGrams,
                    currentWeightGrams = currentWeightGrams
                )
            }
            
            if (filamentId > 0) {
                filamentRepository.updateFilament(filament)
            } else {
                filamentRepository.insertFilament(filament)
            }
        }
    }
    
    class Factory(
        private val repository: FilamentRepository,
        private val filamentId: Long = 0
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FilamentEditViewModel::class.java)) {
                return FilamentEditViewModel(repository, filamentId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}