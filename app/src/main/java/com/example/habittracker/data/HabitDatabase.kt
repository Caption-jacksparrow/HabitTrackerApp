package com.example.habittracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(entities = [Habit::class, HabitEntry::class], version = 1, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(HabitDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class HabitDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.habitDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(habitDao: HabitDao) {
            // Add sample habits when the database is first created
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            val sampleHabit = Habit(
                name = "Morning Meditation",
                description = "10 minutes of mindfulness meditation every morning",
                frequency = "DAILY",
                reminderEnabled = true,
                reminderTime = "08:00",
                color = "#4CAF50",
                startDate = today,
                createdAt = today,
                updatedAt = today
            )
            habitDao.insert(sampleHabit)
        }
    }
}
