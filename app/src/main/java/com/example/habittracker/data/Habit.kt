package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val frequency: String, // DAILY, WEEKLY, MONTHLY, CUSTOM
    val customFrequencyTimes: Int = 0, // For custom frequency: how many times
    val customFrequencyPeriod: String = "", // For custom frequency: per day/week/month
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "", // Stored as HH:mm format
    val color: String, // Hex color code
    val startDate: String, // ISO date format
    val createdAt: String, // ISO date format
    val updatedAt: String // ISO date format
)
