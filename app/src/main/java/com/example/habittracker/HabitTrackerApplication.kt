package com.example.habittracker

import android.app.Application
import androidx.work.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HabitTrackerApplication : Application(), Configuration.Provider {
    // Application scope
    private val applicationScope = CoroutineScope(SupervisorJob())
    
    // Lazy initialize the repository
    val repository by lazy {
        val database = HabitDatabase.getDatabase(this, applicationScope)
        HabitRepository(database.habitDao())
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}
