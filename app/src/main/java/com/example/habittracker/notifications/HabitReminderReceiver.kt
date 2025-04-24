package com.example.habittracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habittracker.HabitTrackerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * BroadcastReceiver for handling habit reminders and related actions
 */
class HabitReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW_REMINDER = "com.example.habittracker.ACTION_SHOW_REMINDER"
        const val ACTION_MARK_DONE = "com.example.habittracker.ACTION_MARK_DONE"
        const val ACTION_DAILY_SUMMARY = "com.example.habittracker.ACTION_DAILY_SUMMARY"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getIntExtra("HABIT_ID", -1)
        
        when (intent.action) {
            ACTION_SHOW_REMINDER -> {
                if (habitId != -1) {
                    showHabitReminder(context, habitId)
                }
            }
            ACTION_MARK_DONE -> {
                if (habitId != -1) {
                    markHabitAsDone(context, habitId)
                }
            }
            ACTION_DAILY_SUMMARY -> {
                showDailySummary(context)
            }
        }
    }
    
    private fun showHabitReminder(context: Context, habitId: Int) {
        val application = context.applicationContext as HabitTrackerApplication
        val repository = application.repository
        
        CoroutineScope(Dispatchers.IO).launch {
            val habit = repository.getHabitById(habitId).first()
            
            // Check if notifications are enabled in preferences
            val sharedPrefs = context.getSharedPreferences("habit_tracker_prefs", 0)
            val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
            
            if (notificationsEnabled && habit != null) {
                val notificationHelper = NotificationHelper(context)
                notificationHelper.showHabitReminderNotification(habit.id, habit.name)
            }
        }
    }
    
    private fun markHabitAsDone(context: Context, habitId: Int) {
        val application = context.applicationContext as HabitTrackerApplication
        val repository = application.repository
        
        CoroutineScope(Dispatchers.IO).launch {
            val habit = repository.getHabitById(habitId).first()
            
            if (habit != null) {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val entry = repository.getEntryForHabitAndDate(habitId, today).first()
                
                if (entry != null) {
                    // Update existing entry
                    repository.updateEntry(entry.copy(completed = true))
                } else {
                    // Create new entry
                    val newEntry = com.example.habittracker.data.HabitEntry(
                        habitId = habitId,
                        date = today,
                        completed = true
                    )
                    repository.insertEntry(newEntry)
                }
                
                // Dismiss the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.cancel(NotificationHelper.NOTIFICATION_ID_REMINDER + habitId)
            }
        }
    }
    
    private fun showDailySummary(context: Context) {
        val application = context.applicationContext as HabitTrackerApplication
        val repository = application.repository
        
        CoroutineScope(Dispatchers.IO).launch {
            // Check if daily summary is enabled in preferences
            val sharedPrefs = context.getSharedPreferences("habit_tracker_prefs", 0)
            val dailySummaryEnabled = sharedPrefs.getBoolean("daily_summary_enabled", false)
            
            if (dailySummaryEnabled) {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val entries = repository.getEntriesForDate(today).first()
                
                val completedCount = entries.count { it.completed }
                val totalCount = entries.size
                
                val notificationHelper = NotificationHelper(context)
                notificationHelper.showDailySummaryNotification(completedCount, totalCount)
            }
        }
    }
}
