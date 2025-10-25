package com.kashmir.thefilamentary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a filament spool in the inventory
 */
@Entity(tableName = "filaments")
data class Filament(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val material: String,
    val color: String,
    val purchasePrice: Double? = null,
    val initialWeightGrams: Int,
    val currentWeightGrams: Int,
    val timestampAdded: Long = System.currentTimeMillis()
)