package com.kashmir.thefilamentary.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kashmir.thefilamentary.data.AppDatabase
import com.kashmir.thefilamentary.data.entity.PrintLog
import com.kashmir.thefilamentary.data.repository.FilamentRepository
import com.kashmir.thefilamentary.data.repository.PrintLogRepository
import com.kashmir.thefilamentary.ui.viewmodel.FilamentDetailViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentDetailScreen(
    filamentId: Long,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddPrintLogClick: () -> Unit,
    onPrintLogClick: (Long) -> Unit,
    viewModel: FilamentDetailViewModel = viewModel(
        factory = FilamentDetailViewModel.Factory(
            filamentId = filamentId,
            filamentRepository = FilamentRepository(
                AppDatabase.getInstance(LocalContext.current).filamentDao()
            ),
            printLogRepository = PrintLogRepository(
                AppDatabase.getInstance(LocalContext.current).printLogDao()
            )
        )
    )
) {
    val filament by viewModel.filament.collectAsState()
    val printLogs by viewModel.printLogs.collectAsState()
    
    var showWeightDialog by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(filament?.let { "${it.brand} ${it.material}" } ?: "Filament Details") 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPrintLogClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Print Log")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            filament?.let { filament ->
                // Filament details card
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
                            text = "${filament.brand} ${filament.material}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Color: ${filament.color}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        filament.purchasePrice?.let {
                            Text(
                                text = "Purchase Price: $${String.format("%.2f", it)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = "Initial Weight: ${filament.initialWeightGrams}g",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Current Weight: ${filament.currentWeightGrams}g",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Update weight button
                        Button(
                            onClick = { 
                                newWeight = filament.currentWeightGrams.toString()
                                showWeightDialog = true 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update Current Weight")
                        }
                    }
                }
                
                // Print logs section
                Text(
                    text = "Print Logs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                if (printLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No print logs yet.\nTap the + button to add one.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(printLogs) { printLog ->
                            PrintLogItem(
                                printLog = printLog,
                                onClick = { onPrintLogClick(printLog.id) }
                            )
                        }
                    }
                }
            } ?: run {
                // Loading or error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Dialog to update weight
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Update Current Weight") },
            text = {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text("Weight (grams)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        newWeight.toIntOrNull()?.let { weight ->
                            viewModel.updateCurrentWeight(weight)
                        }
                        showWeightDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PrintLogItem(
    printLog: PrintLog,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(printLog.timestampLogged))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Left side: Print log details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nozzle: ${printLog.nozzleTempC}°C | Bed: ${printLog.bedTempC}°C",
                    style = MaterialTheme.typography.bodyMedium
                )
                printLog.printSpeedMms?.let {
                    Text(
                        text = "Speed: ${it}mm/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < printLog.outcomeRating) 
                                Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Right side: Thumbnail if available
            printLog.imagePath?.let { path ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(start = 8.dp)
                ) {
                    AsyncImage(
                        model = File(path),
                        contentDescription = "Print result",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}