package com.kashmir.thefilamentary.ui.screen

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kashmir.thefilamentary.data.AppDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isImporting = true
                val success = importDatabase(context, it)
                isImporting = false
                val message = if (success) "Database imported successfully" else "Failed to import database"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Backup & Restore",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Text(
                        text = "Export your database to save all your filament and print log data. " +
                               "You can later import this backup to restore your data.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = {
                            scope.launch {
                                isExporting = true
                                val success = exportDatabase(context)
                                isExporting = false
                                val message = if (success) "Database exported to Downloads folder" else "Failed to export database"
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isExporting && !isImporting
                    ) {
                        Text(if (isExporting) "Exporting..." else "Export Database")
                    }
                    
                    Button(
                        onClick = { importLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isExporting && !isImporting
                    ) {
                        Text(if (isImporting) "Importing..." else "Import Database")
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Text(
                        text = "The Filamentary",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "An offline-first Android application for 3D printing hobbyists to manage filament inventory and log print settings and results.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private suspend fun exportDatabase(context: Context): Boolean = withContext(Dispatchers.IO) {
    try {
        // Close the database
        AppDatabase.closeDatabase()
        
        // Get the database file
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        
        // Create a timestamp for the filename
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val backupFileName = "TheFilamentary_Backup_$timestamp.db"
        
        // Create the destination file in the Downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destFile = File(downloadsDir, backupFileName)
        
        // Copy the database file
        FileInputStream(dbFile).use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        
        // Reopen the database
        AppDatabase.getInstance(context)
        
        true
    } catch (e: Exception) {
        e.printStackTrace()
        // Ensure database is reopened even if export fails
        AppDatabase.getInstance(context)
        false
    }
}

private suspend fun importDatabase(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    try {
        // Close the database
        AppDatabase.closeDatabase()
        
        // Get the database file
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        
        // Copy the selected file to the database location
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw Exception("Could not open input stream")
        
        // Reopen the database
        AppDatabase.getInstance(context)
        
        true
    } catch (e: Exception) {
        e.printStackTrace()
        // Ensure database is reopened even if import fails
        AppDatabase.getInstance(context)
        false
    }
}