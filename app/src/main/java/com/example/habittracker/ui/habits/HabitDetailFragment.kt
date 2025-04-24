package com.example.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentHabitDetailBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitDetailFragment : Fragment() {

    private var _binding: FragmentHabitDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: HabitDetailFragmentArgs by navArgs()
    
    private val habitsViewModel: HabitsViewModel by viewModels {
        HabitsViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val habitId = args.habitId
        
        // Observe the habit details
        habitsViewModel.getHabitById(habitId).observe(viewLifecycleOwner) { habit ->
            habit?.let {
                // Set habit details
                binding.textHabitName.text = it.name
                binding.textHabitDescription.text = it.description
                binding.textFrequencyValue.text = formatFrequency(it.frequency, it.customFrequencyTimes, it.customFrequencyPeriod)
                
                // Set reminder info
                if (it.reminderEnabled && it.reminderTime.isNotEmpty()) {
                    binding.textReminderValue.text = formatTime(it.reminderTime)
                } else {
                    binding.textReminderValue.text = "No reminder set"
                }
                
                // Set start date
                try {
                    val date = LocalDate.parse(it.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    binding.textStartDateValue.text = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                } catch (e: Exception) {
                    binding.textStartDateValue.text = it.startDate
                }
                
                // Set habit color
                try {
                    binding.viewHabitColor.setBackgroundColor(android.graphics.Color.parseColor(it.color))
                } catch (e: Exception) {
                    binding.viewHabitColor.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                }
                
                // Set up statistics
                habitsViewModel.getHabitStatistics(habitId).observe(viewLifecycleOwner) { stats ->
                    binding.textStreak.text = "Current streak: ${stats.currentStreak} days"
                    binding.textCompletionRate.text = "Completion rate: ${stats.completionRate}%"
                    binding.progressBar.progress = stats.completionRate
                }
            }
        }
        
        // Set up button click listeners
        binding.buttonEditHabit.setOnClickListener {
            val action = HabitDetailFragmentDirections.actionHabitDetailToAddEditHabit(habitId)
            findNavController().navigate(action)
        }
        
        binding.buttonDeleteHabit.setOnClickListener {
            habitsViewModel.deleteHabitById(habitId)
            findNavController().navigateUp()
        }
    }
    
    private fun formatFrequency(frequency: String, times: Int, period: String): String {
        return when (frequency) {
            "DAILY" -> "Daily"
            "WEEKLY" -> "Weekly"
            "MONTHLY" -> "Monthly"
            "CUSTOM" -> "$times times per $period"
            else -> frequency
        }
    }
    
    private fun formatTime(time: String): String {
        // Simple time formatting, could be enhanced
        return time
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
