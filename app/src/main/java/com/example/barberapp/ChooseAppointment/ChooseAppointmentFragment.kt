package com.example.barberapp.ChooseAppointment

import ChooseAppointmentViewModel
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
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

        val today = Calendar.getInstance()
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)

        val formattedTodayDate = "${todayYear}-${todayMonth + 1}-${todayDay}"
        tempSelectedDate = formattedTodayDate // Salvar o dia de hoje como selecionado

        binding.calendarView.minDate = today.timeInMillis // Desabilita dias anteriores

        // Observers para slots
        viewModel.morningSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.morningContainer)
        }
        viewModel.afternoonSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.afternoonContainer)
        }

        // Unificado: Listener do calendário com lógica de dias passados
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val isPastDate = selectedDate.before(today.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            })

            if (isPastDate) {
                Toast.makeText(context, "Este dia já passou", Toast.LENGTH_SHORT).show()
                return@setOnDateChangeListener
            }

            // Formatar e salvar a data selecionada
            val formattedDate = "${year}-${month + 1}-${dayOfMonth}"
            tempSelectedDate = formattedDate

            // Limpar os slots antes de carregar os novos horários
            clearSlots()
            val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 2
            viewModel.loadSchedules(barberId, dayOfWeek, formattedDate)
        }

        // Inicializar com o dia de hoje
        initializeTodaySchedule(today, barberId)

        // Configurar botões de "Morning" e "Afternoon"
        configureTimeOfDayButtons()

        // Configurar o botão de salvar
        binding.saveBtn.setOnClickListener {
            saveSelectedTimeAndDate()
        }

        // Configurar o botão de voltar
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeTodaySchedule(today: Calendar, barberId: Int) {
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        binding.calendarView.date = today.timeInMillis // Define a data atual no calendário

        clearSlots()
        val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 2
        val formattedDate = "${todayYear}-${todayMonth + 1}-${todayDay}"
        viewModel.loadSchedules(barberId, todayDayOfWeek, formattedDate)
    }

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

    private fun saveSelectedTimeAndDate() {
        val selectedTime = tempSelectedTime
        val selectedDate = tempSelectedDate
        if (selectedTime != null && selectedDate != null) {
            UserSession.selectedAppointmentTime = selectedTime
            UserSession.selectedAppointmentDate = selectedDate
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(context, "Selecione um horário e uma data antes de salvar.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonStates(activeButton: Button, inactiveButton: Button) {
        activeButton.alpha = 1f
        inactiveButton.alpha = 0.5f
    }

    private fun renderSlots(slots: List<String>, container: GridLayout) {
        container.removeAllViews()
        slots.forEach { slot ->
            val isBlocked = slot.endsWith("*")
            var actualSlot = slot.removeSuffix("*")
            if (isBlocked) return@forEach

            actualSlot = actualSlot.replaceFirst(":00", "")

            val button = Button(requireContext()).apply {
                text = actualSlot
                setBackgroundColor(if (isBlocked) Color.LTGRAY else Color.GRAY)
                isEnabled = !isBlocked

                setOnClickListener {
                    if (isSelected) {
                        isSelected = false
                        setBackgroundColor(Color.GRAY)
                        setTextColor(Color.parseColor("#F6BE00"))
                        tempSelectedTime = null
                        selectedButton = null
                    } else {
                        selectedButton?.isSelected = false
                        selectedButton?.setBackgroundColor(Color.GRAY)
                        selectedButton?.setTextColor(Color.parseColor("#F6BE00"))

                        isSelected = true
                        setBackgroundColor(Color.parseColor("#F6BE00"))
                        setTextColor(Color.BLACK)

                        tempSelectedTime = actualSlot
                        selectedButton = this
                    }
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
                setMargins(4, 4, 4, 4)
            }
            button.layoutParams = params
            container.addView(button)
        }
    }

    private fun clearSlots() {
        binding.morningContainer.removeAllViews()
        binding.afternoonContainer.removeAllViews()
    }
}
