package com.kashmir.thefilamentary.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashmir.thefilamentary.data.AppDatabase
import com.kashmir.thefilamentary.data.repository.FilamentRepository
import com.kashmir.thefilamentary.ui.viewmodel.FilamentEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentEditScreen(
    filamentId: Long? = null,
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: FilamentEditViewModel = viewModel(
        factory = FilamentEditViewModel.Factory(
            repository = FilamentRepository(
                AppDatabase.getInstance(LocalContext.current).filamentDao()
            ),
            filamentId = filamentId ?: 0
        )
    )
) {
    val filamentState by viewModel.filamentState.collectAsState()
    
    // Local state for form fields
    var brand by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var initialWeight by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }
    
    // Track loading and validation state
    var isSaving by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    
    // Update local state when filament data is loaded
    LaunchedEffect(filamentState) {
        filamentState?.let { filament ->
            brand = filament.brand
            material = filament.material
            color = filament.color
            purchasePrice = filament.purchasePrice?.toString() ?: ""
            initialWeight = filament.initialWeightGrams.toString()
            currentWeight = filament.currentWeightGrams.toString()
        }
    }
    
    // Validate form fields
    LaunchedEffect(brand, material, color, initialWeight, currentWeight) {
        isValid = brand.isNotBlank() && 
                 material.isNotBlank() && 
                 color.isNotBlank() && 
                 initialWeight.isNotBlank() && 
                 currentWeight.isNotBlank()
    }
    
    val isEditing = filamentId != null
    val title = if (isEditing) "Edit Filament" else "Add Filament"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Brand
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                modifier = Modifier.fillMaxWidth(),
                isError = brand.isBlank(),
                supportingText = { if (brand.isBlank()) Text("Brand is required") }
            )
            
            // Material
            OutlinedTextField(
                value = material,
                onValueChange = { material = it },
                label = { Text("Material (PLA, PETG, ABS, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                isError = material.isBlank(),
                supportingText = { if (material.isBlank()) Text("Material is required") }
            )
            
            // Color
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth(),
                isError = color.isBlank(),
                supportingText = { if (color.isBlank()) Text("Color is required") }
            )
            
            // Purchase Price
            OutlinedTextField(
                value = purchasePrice,
                onValueChange = { purchasePrice = it },
                label = { Text("Purchase Price (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            // Initial Weight
            OutlinedTextField(
                value = initialWeight,
                onValueChange = { initialWeight = it },
                label = { Text("Initial Weight (grams)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = initialWeight.isBlank(),
                supportingText = { if (initialWeight.isBlank()) Text("Initial weight is required") }
            )
            
            // Current Weight
            OutlinedTextField(
                value = currentWeight,
                onValueChange = { currentWeight = it },
                label = { Text("Current Weight (grams)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = currentWeight.isBlank(),
                supportingText = { if (currentWeight.isBlank()) Text("Current weight is required") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    isSaving = true
                    viewModel.saveFilament(
                        brand = brand,
                        material = material,
                        color = color,
                        purchasePrice = purchasePrice.toDoubleOrNull(),
                        initialWeightGrams = initialWeight.toIntOrNull() ?: 0,
                        currentWeightGrams = currentWeight.toIntOrNull() ?: 0
                    )
                    onSaveComplete()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && isValid
            ) {
                Text(if (isSaving) "Saving..." else "Save Filament")
            }
        }
    }
}