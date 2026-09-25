package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    ChatSessionEntity::class,
    ChatMessageEntity::class,
    HealthProfileEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class MedixDatabase : RoomDatabase() {
  abstract fun medixDao(): MedixDao

  companion object {
    @Volatile
    private var INSTANCE: MedixDatabase? = null

    fun getDatabase(context: Context): MedixDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MedixDatabase::class.java,
          "medix_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
