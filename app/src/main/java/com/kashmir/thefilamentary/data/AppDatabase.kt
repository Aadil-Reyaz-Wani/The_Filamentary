package com.kashmir.thefilamentary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kashmir.thefilamentary.data.dao.FilamentDao
import com.kashmir.thefilamentary.data.dao.PrintLogDao
import com.kashmir.thefilamentary.data.entity.Filament
import com.kashmir.thefilamentary.data.entity.PrintLog

@Database(
    entities = [Filament::class, PrintLog::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
    abstract fun printLogDao(): PrintLogDao

    companion object {
        const val DATABASE_NAME = "filamentary_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
        
        fun closeDatabase() {
            if (INSTANCE?.isOpen == true) {
                INSTANCE?.close()
            }
            INSTANCE = null
        }
    }
}