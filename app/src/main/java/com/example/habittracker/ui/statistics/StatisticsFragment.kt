package com.example.habittracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.databinding.FragmentStatisticsBinding
import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    
    private val statisticsViewModel: StatisticsViewModel by viewModels {
        StatisticsViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up completion rate chart
        setupCompletionChart()
        
        // Observe overall statistics
        statisticsViewModel.overallCompletionRate.observe(viewLifecycleOwner) { completionRate ->
            updateCompletionChart(completionRate)
        }
        
        // Observe habits to get streak information
        statisticsViewModel.allHabits.observe(viewLifecycleOwner) { habits ->
            if (habits.isNotEmpty()) {
                // Get statistics for the first habit (for demonstration)
                val firstHabit = habits[0]
                statisticsViewModel.getHabitStatistics(firstHabit.id).observe(viewLifecycleOwner) { stats ->
                    binding.textCurrentStreak.text = "Current streak: ${stats.currentStreak} days"
                    binding.textLongestStreak.text = "Longest streak: ${stats.longestStreak} days"
                }
            } else {
                binding.textCurrentStreak.text = "Current streak: 0 days"
                binding.textLongestStreak.text = "Longest streak: 0 days"
            }
        }
    }
    
    private fun setupCompletionChart() {
        val chart = PieChart(requireContext())
        binding.completionChartContainer.addView(chart)
        
        chart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleAlpha(0)
            holeRadius = 58f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            setUsePercentValues(true)
            setCenterTextSize(16f)
        }
        
        // Default empty chart
        updateCompletionChart(0)
    }
    
    private fun updateCompletionChart(completionRate: Int) {
        val chart = binding.completionChartContainer.getChildAt(0) as PieChart
        
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(completionRate.toFloat(), "Completed"))
        entries.add(PieEntry((100 - completionRate).toFloat(), "Missed"))
        
        val dataSet = PieDataSet(entries, "Completion Rate")
        dataSet.apply {
            sliceSpace = 3f
            selectionShift = 5f
            colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
        }
        
        val data = PieData(dataSet)
        data.apply {
            setValueFormatter(PercentFormatter(chart))
            setValueTextSize(14f)
            setValueTextColor(Color.WHITE)
        }
        
        chart.apply {
            this.data = data
            centerText = "$completionRate%"
            invalidate()
            animateY(1000)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
