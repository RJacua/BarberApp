package com.example.barberapp.ChooseAppointment

import AppointmentViewModel
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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentAppointmentBinding
import java.util.Calendar

class AppointmentFragment : Fragment() {

    private val viewModel by viewModels<AppointmentViewModel>()

    private lateinit var binding: FragmentAppointmentBinding

    private var selectedButton: Button? = null

    // Variável para armazenar o horário selecionado temporariamente
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

        // Observers para slots
        viewModel.morningSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.morningContainer)
        }
        viewModel.afternoonSlots.observe(viewLifecycleOwner) { slots ->
            renderSlots(slots, binding.afternoonContainer)
        }

        // Listener do calendário
        binding.calendarView.minDate = today.timeInMillis // Desabilita dias anteriores
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 2 // Ajusta para sua lógica
            val formattedDate = "${year}-${month + 1}-${dayOfMonth}" // Formato de data esperado

            tempSelectedDate = formattedDate // Armazena a data temporariamente

            // Limpar os contêineres antes de carregar os novos horários
            clearSlots()

            // Carregar os horários do novo dia
            viewModel.loadSchedules(barberId, dayOfWeek, formattedDate)
        }


        // Inicializar com o dia de hoje
        val todayYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        binding.calendarView.date = today.timeInMillis // Define a data atual no calendário

        // Limpar e carregar os horários para hoje
        clearSlots()
        val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 2
        val formattedDate = "${todayYear}-${todayMonth + 1}-${todayDay}"
        viewModel.loadSchedules(barberId, todayDayOfWeek,formattedDate)


        // Inicializar opacidade padrão
        updateButtonStates(binding.morningLabel, binding.afternoonLabel)

        // Clique em "Morning"
        binding.morningLabel.setOnClickListener {
            binding.morningContainer.visibility = View.VISIBLE
            binding.afternoonContainer.visibility = View.GONE
            updateButtonStates(binding.morningLabel, binding.afternoonLabel)
        }

        // Clique em "Afternoon"
        binding.afternoonLabel.setOnClickListener {
            binding.morningContainer.visibility = View.GONE
            binding.afternoonContainer.visibility = View.VISIBLE
            updateButtonStates(binding.afternoonLabel, binding.morningLabel)
        }

        // Configuração do botão salvar
        binding.saveBtn.setOnClickListener {
            val selectedTime = tempSelectedTime
            val selectedDate = tempSelectedDate
            if (selectedTime != null  && selectedDate != null) {
                // Salvar no UserSession
                UserSession.selectedAppointmentTime = selectedTime
                UserSession.selectedAppointmentDate = selectedDate
                Toast.makeText(context, "Horário salvo: $selectedTime em $selectedDate\"", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Selecione um horário e uma data antes de salvar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão "Back"
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp() // Volta para o fragmento anterior
        }
    }

    private fun updateButtonStates(activeButton: Button, inactiveButton: Button) {
        // Botão ativo (texto com opacidade normal)
        activeButton.alpha = 1f

        // Botão inativo (texto com opacidade reduzida)
        inactiveButton.alpha = 0.5f
    }


    private fun renderSlots(slots: List<String>, container: GridLayout) {
        container.removeAllViews() // Remove botões antigos
        slots.forEach { slot ->
            val isBlocked = slot.endsWith("*") // Indicador para bloqueado (conforme lógica do ViewModel)
            var actualSlot = slot.removeSuffix("*") // Remove o marcador antes de exibir

            actualSlot = actualSlot.split(":")[0] + ":" + actualSlot.split(":")[1]

            /*if (actualSlot.split(":")[0] == "09") {
                actualSlot = actualSlot.replace("09", "9")
            }*/

            // Se o horário está bloqueado, não cria o botão
            if (isBlocked) return@forEach

            val button = Button(requireContext()).apply {
                text = actualSlot
                setBackgroundColor(if (isBlocked) Color.LTGRAY else Color.GRAY)
                isEnabled = !isBlocked // Desabilita clique se o horário estiver bloqueado

                setOnClickListener {
                    if (isSelected) {
                        // Se o botão já estiver marcado, desmarque-o
                        isSelected = false
                        setBackgroundColor(Color.GRAY)
                        tempSelectedTime = null
                        selectedButton = null
                    } else {
                        // Desmarcar o botão anterior, se existir
                        selectedButton?.isSelected = false
                        selectedButton?.setBackgroundColor(Color.GRAY)

                        // Marcar o botão atual como selecionado
                        isSelected = true
                        setBackgroundColor(Color.parseColor("#4CAF50"))

                        tempSelectedTime = actualSlot
                        selectedButton = this
                    }
                }
            }



            // Adiciona o botão ao GridLayout
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


    // Função para limpar os contêineres
    private fun clearSlots() {
        binding.morningContainer.removeAllViews()
        binding.afternoonContainer.removeAllViews()
    }


    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

}