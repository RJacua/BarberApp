package com.example.barberapp.ChooseAppointment

import ChooseAppointmentViewModel
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentAppointmentBinding
import java.util.Calendar

class ChooseAppointmentFragment : Fragment() {

    private val viewModel by viewModels<ChooseAppointmentViewModel>()
    private lateinit var binding: FragmentAppointmentBinding
    private var selectedButton: Button? = null
    private var tempSelectedTime: String? = null
    private var tempSelectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val barberId = UserSession.selectedBarberId!!

        // Get the current date
        val today = Calendar.getInstance()
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)

        // Format the current date
        val formattedTodayDate = "${todayYear}-${todayMonth + 1}-${todayDay}"
        tempSelectedDate = formattedTodayDate // Save today's date as selected

        // Disable past dates in the calendar
        binding.calendarView.minDate = today.timeInMillis

        // Observe changes in morning slots
        viewModel.morningSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.morningContainer)
        }
        // Observe changes in afternoon slots
        viewModel.afternoonSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.afternoonContainer)
        }

        // Handle date selection in the calendar
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            // Check if the selected date is in the past
            val isPastDate = selectedDate.before(today.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            })

            if (isPastDate) {
                Toast.makeText(context, "This date has already passed", Toast.LENGTH_SHORT).show()
                return@setOnDateChangeListener
            }

            // Format and save the selected date
            val formattedDate = "${year}-${month + 1}-${dayOfMonth}"
            tempSelectedDate = formattedDate

            // Clear current time slots before loading new ones
            clearSlots()

            val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 2
            viewModel.loadSchedules(barberId, dayOfWeek, formattedDate)
        }

        // Initialize the schedule for today
        initializeTodaySchedule(today, barberId)

        // Configure "Morning" and "Afternoon" buttons
        configureTimeOfDayButtons()

        binding.saveBtn.setOnClickListener {
            saveSelectedTimeAndDate()
        }

    }

    /**
     * Initialize today schedule
     * Initialize the calendar with today's date
     *
     * @param today
     * @param barberId
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeTodaySchedule(today: Calendar, barberId: Int) {
        // Initialize the calendar with today's date
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        binding.calendarView.date = today.timeInMillis

        // Clear current time slots
        clearSlots()

        val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 2
        val formattedDate = "${todayYear}-${todayMonth + 1}-${todayDay}"
        viewModel.loadSchedules(barberId, todayDayOfWeek, formattedDate)
    }

    /**
     * Configure time of day buttons
     * Configure buttons to switch between "Morning" and "Afternoon"
     *
     */
    private fun configureTimeOfDayButtons() {
        updateButtonStates(binding.morningLabel, binding.afternoonLabel)

        binding.morningLabel.setOnClickListener {
            binding.morningContainer.visibility = View.VISIBLE
            binding.afternoonContainer.visibility = View.GONE
            updateButtonStates(binding.morningLabel, binding.afternoonLabel)
        }

        binding.afternoonLabel.setOnClickListener {
            binding.morningContainer.visibility = View.GONE
            binding.afternoonContainer.visibility = View.VISIBLE
            updateButtonStates(binding.afternoonLabel, binding.morningLabel)
        }
    }

    /**
     * Save selected time and date
     * Save the selected time and date chosen by the client
     *
     */
    private fun saveSelectedTimeAndDate() {
        val selectedTime = tempSelectedTime
        val selectedDate = tempSelectedDate
        if (selectedTime != null && selectedDate != null) {
            UserSession.selectedAppointmentTime = selectedTime
            UserSession.selectedAppointmentDate = selectedDate
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(context, "Select a time and date before saving.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Update button states
     *
     * @param activeButton
     * @param inactiveButton
     */
    private fun updateButtonStates(activeButton: Button, inactiveButton: Button) {
        activeButton.alpha = 1f
        inactiveButton.alpha = 0.5f
    }


    /**
     * Render slots
     * Renders the available time slots in a layout container.
     * For each slot, creates an interactive button allowing the client to select a time.
     * Blocked (inactive) slots are ignored.
     *
     * @param slots
     * @param container
     */
    private fun renderSlots(slots: List<String>, container: GridLayout) {
        // Render available time slots
        container.removeAllViews()
        slots.forEach { slot ->
            var actualSlot = slot.replaceFirst(":00", "")

            // Create a button for each available time slot
            val button = Button(requireContext()).apply {
                text = actualSlot
                background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_time_slot)
                isEnabled = true

                setOnClickListener {
                    // Se houver um botão já selecionado, resetar para o estado original
                    selectedButton?.apply {
                        isSelected = false
                        setTextColor(Color.parseColor("#F6BE00")) // 🔹 Volta o texto para amarelo
                    }

                    // Atualizar o botão atual como selecionado
                    if (isSelected) {
                        isSelected = false
                        tempSelectedTime = null
                        selectedButton = null
                        setTextColor(Color.parseColor("#F6BE00")) // 🔹 Voltar o texto para amarelo
                    } else {
                        selectedButton = this
                        isSelected = true
                        tempSelectedTime = actualSlot
                        setTextColor(Color.BLACK) // 🔹 Texto preto no botão selecionado
                    }
                }
            }

            // Set margins and layout for the button
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
                setMargins(16, 16, 16, 16)
            }
            button.layoutParams = params
            container.addView(button)
        }
    }

    /**
     * Clear slots
     * Clear the displayed time slots
     *
     */
    private fun clearSlots() {
        binding.morningContainer.removeAllViews()
        binding.afternoonContainer.removeAllViews()
    }
}
