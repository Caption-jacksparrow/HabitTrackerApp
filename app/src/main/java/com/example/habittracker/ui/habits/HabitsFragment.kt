package com.example.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentHabitsBinding

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private val habitsViewModel: HabitsViewModel by viewModels {
        HabitsViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = HabitAdapter(
            requireContext(),
            viewLifecycleOwner,
            habitsViewModel,
            onItemClick = { habit ->
                val action = HabitsFragmentDirections.actionHabitsToHabitDetail(habit.id)
                findNavController().navigate(action)
            }
        )
        
        binding.recyclerHabits.adapter = adapter
        binding.recyclerHabits.layoutManager = LinearLayoutManager(context)
        
        // Observe the habits LiveData
        habitsViewModel.allHabits.observe(viewLifecycleOwner) { habits ->
            habits.let { adapter.submitList(it) }
            
            // Show empty state if no habits
            if (habits.isEmpty()) {
                binding.textNoHabits.visibility = View.VISIBLE
            } else {
                binding.textNoHabits.visibility = View.GONE
            }
        }
        
        // Set up FAB click listener
        binding.fabAddHabit.setOnClickListener {
            val action = HabitsFragmentDirections.actionHabitsToAddHabit()
            findNavController().navigate(action)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
