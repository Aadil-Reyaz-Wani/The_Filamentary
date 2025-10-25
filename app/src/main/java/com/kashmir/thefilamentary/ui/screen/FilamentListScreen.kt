package com.kashmir.thefilamentary.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashmir.thefilamentary.data.AppDatabase
import com.kashmir.thefilamentary.data.entity.Filament
import com.kashmir.thefilamentary.data.repository.FilamentRepository
import com.kashmir.thefilamentary.ui.viewmodel.FilamentListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListScreen(
    onFilamentClick: (Long) -> Unit,
    onAddFilamentClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: FilamentListViewModel = viewModel(
        factory = FilamentListViewModel.Factory(
            FilamentRepository(
                AppDatabase.getInstance(androidx.compose.ui.platform.LocalContext.current).filamentDao()
            )
        )
    )
) {
    val filaments by viewModel.filaments.collectAsState()
    val filamentCount by viewModel.filamentCount.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filament Inventory") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFilamentClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Filament")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary stats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Total Spools: $filamentCount",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            // Filament list
            if (filaments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No filaments added yet.\nTap the + button to add one.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filaments) { filament ->
                        FilamentItem(
                            filament = filament,
                            onClick = { onFilamentClick(filament.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilamentItem(
    filament: Filament,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = filament.brand,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${filament.material} - ${filament.color}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Current Weight: ${filament.currentWeightGrams}g / ${filament.initialWeightGrams}g",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}