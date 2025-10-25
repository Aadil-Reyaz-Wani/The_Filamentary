package com.kashmir.thefilamentary.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kashmir.thefilamentary.data.AppDatabase
import com.kashmir.thefilamentary.data.repository.PrintLogRepository
import com.kashmir.thefilamentary.ui.viewmodel.PrintLogEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintLogEditScreen(
    filamentId: Long,
    printLogId: Long? = null,
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: PrintLogEditViewModel = viewModel(
        factory = PrintLogEditViewModel.Factory(
            repository = PrintLogRepository(
                AppDatabase.getInstance(LocalContext.current).printLogDao()
            ),
            filamentId = filamentId,
            printLogId = printLogId ?: 0L
        )
    )
) {
    val printLogState by viewModel.printLogState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    
    val isEditing = printLogId != null && printLogId > 0
    val title = if (isEditing) "Edit Print Log" else "Add Print Log"
    
    val context = LocalContext.current
    
    // State for form fields
    var nozzleTemp by remember { mutableStateOf("") }
    var bedTemp by remember { mutableStateOf("") }
    var printSpeed by remember { mutableStateOf("") }
    var filamentUsed by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }
    
    // Initialize form fields from existing print log if editing
    LaunchedEffect(printLogState) {
        printLogState?.let { printLog ->
            nozzleTemp = printLog.nozzleTempC.toString()
            bedTemp = printLog.bedTempC.toString()
            printLog.printSpeedMms?.let { printSpeed = it.toString() }
            printLog.filamentUsedGrams?.let { filamentUsed = it.toString() }
            rating = printLog.outcomeRating
            printLog.notes?.let { notes = it }
        }
    }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
            // Nozzle Temperature
            OutlinedTextField(
                value = nozzleTemp,
                onValueChange = { nozzleTemp = it },
                label = { Text("Nozzle Temperature (°C)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Bed Temperature
            OutlinedTextField(
                value = bedTemp,
                onValueChange = { bedTemp = it },
                label = { Text("Bed Temperature (°C)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Print Speed
            OutlinedTextField(
                value = printSpeed,
                onValueChange = { printSpeed = it },
                label = { Text("Print Speed (mm/s, optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Filament Used
            OutlinedTextField(
                value = filamentUsed,
                onValueChange = { filamentUsed = it },
                label = { Text("Filament Used (grams, optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Outcome Rating
            Text("Outcome Rating")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                repeat(5) { index ->
                    val starRating = index + 1
                    IconButton(onClick = { rating = starRating }) {
                        Icon(
                            imageVector = if (starRating <= rating) 
                                Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Rating $starRating",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Image Selection
            Text("Upload Photo")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upload Photo")
                }
            }
            
            // Image Preview
            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { imagePicker.launch("image/*") }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FilledTonalButton(
                onClick = { 
                    viewModel.savePrintLog(
                        context = context,
                        nozzleTempC = nozzleTemp.toIntOrNull() ?: 0,
                        bedTempC = bedTemp.toIntOrNull() ?: 0,
                        printSpeedMms = printSpeed.toIntOrNull(),
                        outcomeRating = rating,
                        notes = notes.takeIf { it.isNotBlank() },
                        filamentUsedGrams = filamentUsed.toIntOrNull()
                    )
                    onSaveComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Log")
            }
        }
    }
}