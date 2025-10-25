package com.kashmir.thefilamentary.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing a print log entry associated with a filament
 */
@Entity(
    tableName = "print_logs",
    foreignKeys = [
        ForeignKey(
            entity = Filament::class,
            parentColumns = ["id"],
            childColumns = ["filamentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PrintLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filamentId: Long,
    val timestampLogged: Long = System.currentTimeMillis(),
    val nozzleTempC: Int,
    val bedTempC: Int,
    val printSpeedMms: Int? = null,
    val outcomeRating: Int, // 1-5 stars
    val notes: String? = null,
    val filamentUsedGrams: Int? = null,
    val imagePath: String? = null // Path to the compressed image in internal storage
)