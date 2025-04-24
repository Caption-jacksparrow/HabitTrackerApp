package com.example.habittracker.ui.habits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitEntry
import android.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: HabitsViewModel,
    private val onItemClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, context, lifecycleOwner, viewModel, onItemClick)
    }

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitNameTextView: TextView = itemView.findViewById(R.id.text_habit_name)
        private val habitDescriptionTextView: TextView = itemView.findViewById(R.id.text_habit_description)
        private val habitColorView: View = itemView.findViewById(R.id.view_habit_color)
        private val habitCompletedCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_habit_completed)
        
        private var completionObserver: Observer<Boolean>? = null
        private var currentHabitId: Int = -1

        fun bind(
            habit: Habit, 
            context: Context,
            lifecycleOwner: LifecycleOwner,
            viewModel: HabitsViewModel,
            onItemClick: (Habit) -> Unit
        ) {
            habitNameTextView.text = habit.name
            habitDescriptionTextView.text = habit.description
            
            try {
                habitColorView.setBackgroundColor(Color.parseColor(habit.color))
            } catch (e: Exception) {
                // Default to primary color if parsing fails
                habitColorView.setBackgroundColor(Color.parseColor("#4CAF50"))
            }
            
            // Set click listener for the whole item
            itemView.setOnClickListener {
                onItemClick(habit)
            }
            
            // Remove previous observer if exists
            if (completionObserver != null && currentHabitId != habit.id) {
                viewModel.getHabitCompletionForToday(currentHabitId)
                    .removeObserver(completionObserver!!)
                completionObserver = null
            }
            
            // Set up checkbox state based on today's completion status
            if (completionObserver == null || currentHabitId != habit.id) {
                currentHabitId = habit.id
                
                completionObserver = Observer<Boolean> { isCompleted ->
                    // Update checkbox without triggering the listener
                    habitCompletedCheckbox.setOnCheckedChangeListener(null)
                    habitCompletedCheckbox.isChecked = isCompleted
                    
                    // Re-establish the listener
                    habitCompletedCheckbox.setOnCheckedChangeListener { _, checked ->
                        viewModel.toggleHabitCompletion(habit, checked)
                    }
                }
                
                viewModel.getHabitCompletionForToday(habit.id)
                    .observe(lifecycleOwner, completionObserver!!)
            }
        }
    }

    class HabitComparator : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}
