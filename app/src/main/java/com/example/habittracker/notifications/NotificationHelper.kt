package com.example.habittracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.habittracker.R
import com.example.habittracker.ui.MainActivity

/**
 * Utility class for handling notifications in the app
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_REMINDERS = "habit_reminders"
        const val CHANNEL_ID_SUMMARIES = "habit_summaries"
        
        const val NOTIFICATION_ID_REMINDER = 1000
        const val NOTIFICATION_ID_SUMMARY = 2000
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        // Create notification channels for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to complete your habits"
                enableVibration(true)
            }
            
            val summaryChannel = NotificationChannel(
                CHANNEL_ID_SUMMARIES,
                "Daily Summaries",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily summaries of your habit progress"
            }
            
            // Register the channels with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(summaryChannel)
        }
    }
    
    /**
     * Show a habit reminder notification
     */
    fun showHabitReminderNotification(habitId: Int, habitName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("HABIT_ID", habitId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            habitId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_habits)
            .setContentTitle("Time for your habit")
            .setContentText("Don't forget to $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_habits,
                "Mark as Done",
                createMarkAsDonePendingIntent(habitId)
            )
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_REMINDER + habitId, builder.build())
        }
    }
    
    /**
     * Show a daily summary notification
     */
    fun showDailySummaryNotification(completedCount: Int, totalCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val completionRate = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_SUMMARIES)
            .setSmallIcon(R.drawable.ic_statistics)
            .setContentTitle("Daily Habit Summary")
            .setContentText("You completed $completedCount out of $totalCount habits today ($completionRate%)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_SUMMARY, builder.build())
        }
    }
    
    /**
     * Create a PendingIntent for marking a habit as done from the notification
     */
    private fun createMarkAsDonePendingIntent(habitId: Int): PendingIntent {
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = HabitReminderReceiver.ACTION_MARK_DONE
            putExtra("HABIT_ID", habitId)
        }
        
        return PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
