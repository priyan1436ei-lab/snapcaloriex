package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MealEntity::class, FoodItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CalorieLensDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: CalorieLensDatabase? = null

        fun getDatabase(context: Context): CalorieLensDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalorieLensDatabase::class.java,
                    "calorie_lens.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
