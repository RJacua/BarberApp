package com.example.barberapp.SetBarberSchedule

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentBarberScheduleBinding

class BarberScheduleFragment : Fragment() {

    private lateinit var binding: FragmentBarberScheduleBinding

    private val viewModel by activityViewModels<ScheduleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if the barber is logged...
        val barberId = UserSession.loggedInBarber?.barberId;
        if (barberId == null) {
            // And redirect the user to the login page if it is not
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToLoginFragment())
            return
        }

        // Populate the schedule table created by the createScheduleTable() function based on the logged barber's schedule
        viewModel.getBarberSchedules(barberId) { scheduleMap ->
            createScheduleTable(scheduleMap)
        }

        // Back to home button
        binding.btnBackScheduleBackToHome.setOnClickListener {
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }

        // Save schedule changes button
        binding.btnSaveSchedule.setOnClickListener {
            saveBarberSchedule(barberId)
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }
    }


    /**
     * Create the view for the schedule table and its functionalities
     *
     * @param scheduleMap
     */
    private fun createScheduleTable(scheduleMap: Map<Int, List<Int>>) {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val hours = listOf(
            "9h", "10h", "11h", "12h",
            "13h", "14h", "15h", "16h",
            "17h", "18h"
        )

        val tableLayout = binding.tableLayoutSchedule
        tableLayout.removeAllViews()

        val backgroundColors = listOf("#55FFFFFF", "#33FFFFFF")

        // Header
        val headerRow = TableRow(requireContext())
        for ((index, day) in daysOfWeek.withIndex()) {
            val dayCell = TextView(requireContext()).apply {
                text = day
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundColor(Color.parseColor(backgroundColors[index % 2]))
                setTextColor(Color.parseColor("#F6BE00"))
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }
            headerRow.addView(dayCell)
        }
        tableLayout.addView(headerRow)

        // Lines with buttons
        for (hourIndex in hours.indices) {
            val row = TableRow(requireContext())
            for ((dayIndex, day) in daysOfWeek.withIndex()) {
                val button = Button(requireContext()).apply {
                    text = hours[hourIndex]
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    setBackgroundColor(Color.parseColor(backgroundColors[dayIndex % 2]))
                    setTextColor(Color.parseColor("#F6BE00"))
                    isAllCaps = false

                    val hour = hourIndex + 9
                    if (scheduleMap[dayIndex]?.contains(hour) == true) {
                        setBackgroundColor(Color.parseColor("#F6BE00"))
                        setTextColor(Color.parseColor("#000000"))
                        tag = "selected"
                    } else {
                        tag = "unselected"
                    }

                    setOnClickListener {
                        toggleButtonState(this, backgroundColors[dayIndex % 2])
                    }
                }
                row.addView(button)
            }
            tableLayout.addView(row)
        }
    }


    /**
     * Toggle state of the schedule table's button (based on the button pressed)
     *
     * @param button
     * @param originalColor
     */
    private fun toggleButtonState(button: Button, originalColor: String) {
        if (button.tag == "selected") {
            button.tag = "unselected"
            button.setBackgroundColor(Color.parseColor(originalColor))
            button.setTextColor(Color.parseColor("#F6BE00"))
        } else {
            button.tag = "selected"
            button.setBackgroundColor(Color.parseColor("#F6BE00"))
            button.setTextColor(Color.BLACK)
        }
    }


    /**
     * Save barber schedule based on the buttons selected in the schedule table
     *
     * @param barberId
     */
    private fun saveBarberSchedule(barberId: Int) {
        val scheduleMap = mutableMapOf<Int, MutableList<Int>>()

        for (i in 1 until binding.tableLayoutSchedule.childCount) {
            val row = binding.tableLayoutSchedule.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val button = row.getChildAt(j) as Button
                if (button.tag == "selected") {
                    val dayOfWeek = j
                    val hour = button.text.toString().removeSuffix("h").toInt()
                    scheduleMap.getOrPut(dayOfWeek) { mutableListOf() }.add(hour)
                }
            }
        }

        viewModel.saveBarberSchedule(barberId, scheduleMap)
        Toast.makeText(requireContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show()
    }
}
