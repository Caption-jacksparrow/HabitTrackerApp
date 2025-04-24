package com.example.habittracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.habittracker.BuildConfig
import com.example.habittracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set version info
        binding.textVersion.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        
        // Set up notification settings
        setupNotificationSettings()
        
        // Set up theme settings
        setupThemeSettings()
        
        // Set up data management buttons
        setupDataManagementButtons()
    }
    
    private fun setupNotificationSettings() {
        // Load saved preferences
        val sharedPrefs = requireActivity().getSharedPreferences("habit_tracker_prefs", 0)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val dailySummaryEnabled = sharedPrefs.getBoolean("daily_summary_enabled", false)
        
        // Set initial switch states
        binding.switchEnableNotifications.isChecked = notificationsEnabled
        binding.switchDailySummary.isChecked = dailySummaryEnabled
        
        // Set up listeners
        binding.switchEnableNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            binding.switchDailySummary.isEnabled = isChecked
        }
        
        binding.switchDailySummary.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("daily_summary_enabled", isChecked).apply()
        }
        
        // Set initial enabled state
        binding.switchDailySummary.isEnabled = notificationsEnabled
    }
    
    private fun setupThemeSettings() {
        // Load saved theme preference
        val sharedPrefs = requireActivity().getSharedPreferences("habit_tracker_prefs", 0)
        val themeMode = sharedPrefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        // Set initial radio button selection
        when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> binding.radioThemeSystem.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding.radioThemeLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.radioThemeDark.isChecked = true
        }
        
        // Set up listeners
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val newThemeMode = when (checkedId) {
                binding.radioThemeSystem.id -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                binding.radioThemeLight.id -> AppCompatDelegate.MODE_NIGHT_NO
                binding.radioThemeDark.id -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            
            // Save preference
            sharedPrefs.edit().putInt("theme_mode", newThemeMode).apply()
            
            // Apply theme change
            AppCompatDelegate.setDefaultNightMode(newThemeMode)
        }
    }
    
    private fun setupDataManagementButtons() {
        binding.buttonExportData.setOnClickListener {
            // TODO: Implement data export functionality
            Toast.makeText(context, "Export functionality will be implemented soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.buttonImportData.setOnClickListener {
            // TODO: Implement data import functionality
            Toast.makeText(context, "Import functionality will be implemented soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.buttonClearData.setOnClickListener {
            // Show confirmation dialog before clearing data
            showClearDataConfirmationDialog()
        }
    }
    
    private fun showClearDataConfirmationDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to delete all habits and tracking data? This action cannot be undone.")
            .setPositiveButton("Clear Data") { _, _ ->
                // TODO: Implement data clearing functionality
                Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
