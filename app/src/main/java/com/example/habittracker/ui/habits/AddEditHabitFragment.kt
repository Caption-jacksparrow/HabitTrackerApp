package com.example.habittracker.ui.habits

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.FragmentAddEditHabitBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AddEditHabitFragment : Fragment() {

    private var _binding: FragmentAddEditHabitBinding? = null
    private val binding get() = _binding!!
    
    private val args: AddEditHabitFragmentArgs by navArgs()
    
    private val habitsViewModel: HabitsViewModel by viewModels {
        HabitsViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }
    
    private var selectedColor = "#4CAF50" // Default color
    private var selectedDate = LocalDate.now()
    private var selectedTime = LocalTime.of(8, 0) // Default time: 8:00 AM
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditHabitBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCustomFrequencyControls()
        setupReminderControls()
        setupColorPicker()
        setupDatePicker()
        
        val habitId = args.habitId
        if (habitId != -1) {
            // Edit mode
            habitsViewModel.getHabitById(habitId).observe(viewLifecycleOwner) { habit ->
                habit?.let { populateUI(it) }
            }
        } else {
            // Add mode
            updateDateButtonText()
            updateTimeButtonText()
        }
        
        // Set up save button
        binding.buttonSaveHabit.setOnClickListener {
            saveHabit()
        }
        
        // Set up cancel button
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupCustomFrequencyControls() {
        // Set up custom frequency period spinner
        val periodAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.custom_frequency_periods,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCustomPeriod.adapter = periodAdapter
        
        // Show/hide custom frequency controls based on radio selection
        binding.radioGroupFrequency.setOnCheckedChangeListener { _, checkedId ->
            binding.layoutCustomFrequency.visibility = 
                if (checkedId == R.id.radio_custom) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupReminderControls() {
        // Enable/disable time picker based on switch
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.buttonTimePicker.isEnabled = isChecked
        }
        
        // Set up time picker
        binding.buttonTimePicker.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    selectedTime = LocalTime.of(hourOfDay, minute)
                    updateTimeButtonText()
                },
                selectedTime.hour,
                selectedTime.minute,
                false
            ).show()
        }
    }
    
    private fun setupColorPicker() {
        // Create color buttons dynamically
        val colors = resources.getStringArray(R.array.habit_colors)
        val colorLayout = binding.layoutColors
        
        colors.forEach { colorHex ->
            val colorButton = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(60, 60).apply {
                    setMargins(8, 8, 8, 8)
                }
                setBackgroundColor(android.graphics.Color.parseColor(colorHex))
                tag = colorHex
                
                // Add border to selected color
                if (colorHex == selectedColor) {
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_color_circle)
                }
                
                setOnClickListener {
                    selectedColor = colorHex
                    updateSelectedColorButton()
                }
            }
            colorLayout.addView(colorButton)
        }
    }
    
    private fun updateSelectedColorButton() {
        // Update UI to show selected color
        for (i in 0 until binding.layoutColors.childCount) {
            val child = binding.layoutColors.getChildAt(i)
            val colorHex = child.tag as String
            
            if (colorHex == selectedColor) {
                child.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_color_circle)
            } else {
                child.setBackgroundColor(android.graphics.Color.parseColor(colorHex))
            }
        }
    }
    
    private fun setupDatePicker() {
        binding.buttonDatePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateDateButtonText()
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            ).show()
        }
    }
    
    private fun updateDateButtonText() {
        binding.buttonDatePicker.text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
    
    private fun updateTimeButtonText() {
        binding.buttonTimePicker.text = selectedTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }
    
    private fun populateUI(habit: Habit) {
        binding.editHabitName.setText(habit.name)
        binding.editHabitDescription.setText(habit.description)
        
        // Set frequency
        when (habit.frequency) {
            "DAILY" -> binding.radioDaily.isChecked = true
            "WEEKLY" -> binding.radioWeekly.isChecked = true
            "MONTHLY" -> binding.radioMonthly.isChecked = true
            "CUSTOM" -> {
                binding.radioCustom.isChecked = true
                binding.editCustomTimes.setText(habit.customFrequencyTimes.toString())
                
                // Set spinner selection
                val periodAdapter = binding.spinnerCustomPeriod.adapter as ArrayAdapter<String>
                val position = periodAdapter.getPosition(habit.customFrequencyPeriod)
                if (position >= 0) {
                    binding.spinnerCustomPeriod.setSelection(position)
                }
            }
        }
        
        // Set reminder
        binding.switchReminder.isChecked = habit.reminderEnabled
        binding.buttonTimePicker.isEnabled = habit.reminderEnabled
        
        if (habit.reminderTime.isNotEmpty()) {
            try {
                val parts = habit.reminderTime.split(":")
                if (parts.size == 2) {
                    selectedTime = LocalTime.of(parts[0].toInt(), parts[1].toInt())
                    updateTimeButtonText()
                }
            } catch (e: Exception) {
                // Use default time if parsing fails
            }
        }
        
        // Set color
        selectedColor = habit.color
        updateSelectedColorButton()
        
        // Set start date
        try {
            selectedDate = LocalDate.parse(habit.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            selectedDate = LocalDate.now()
        }
        updateDateButtonText()
    }
    
    private fun saveHabit() {
        val name = binding.editHabitName.text.toString().trim()
        val description = binding.editHabitDescription.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.inputLayoutHabitName.error = "Name cannot be empty"
            return
        }
        
        // Get frequency
        val frequency = when (binding.radioGroupFrequency.checkedRadioButtonId) {
            R.id.radio_daily -> "DAILY"
            R.id.radio_weekly -> "WEEKLY"
            R.id.radio_monthly -> "MONTHLY"
            R.id.radio_custom -> "CUSTOM"
            else -> "DAILY"
        }
        
        // Get custom frequency details
        val customFrequencyTimes = if (frequency == "CUSTOM") {
            binding.editCustomTimes.text.toString().toIntOrNull() ?: 1
        } else {
            0
        }
        
        val customFrequencyPeriod = if (frequency == "CUSTOM") {
            binding.spinnerCustomPeriod.selectedItem.toString()
        } else {
            ""
        }
        
        // Get reminder details
        val reminderEnabled = binding.switchReminder.isChecked
        val reminderTime = if (reminderEnabled) {
            String.format("%02d:%02d", selectedTime.hour, selectedTime.minute)
        } else {
            ""
        }
        
        // Create or update habit
        val habit = Habit(
            id = if (args.habitId != -1) args.habitId else 0,
            name = name,
            description = description,
            frequency = frequency,
            customFrequencyTimes = customFrequencyTimes,
            customFrequencyPeriod = customFrequencyPeriod,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime,
            color = selectedColor,
            startDate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            createdAt = if (args.habitId != -1) {
                habitsViewModel.getHabitById(args.habitId).value?.createdAt ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            } else {
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            },
            updatedAt = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
        
        if (args.habitId != -1) {
            habitsViewModel.update(habit)
        } else {
            habitsViewModel.insert(habit)
        }
        
        findNavController().navigateUp()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
