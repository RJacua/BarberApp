package com.example.barberapp.BarberSchedule

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

    // Usar LoginViewModel para gerenciar o estado global
    private val loginViewModel: LoginViewModel by activityViewModels()

    private val viewModel by activityViewModels<ScheduleViewModel>() // ViewModel para gerenciar os dados

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verifica se o barbeiro está logado
        val barberId = UserSession.loggedInBarber?.barberId;
        if (barberId == null) {
            // Redireciona para a tela de login se o barbeiro não estiver logado
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToLoginFragment())
            return
        }

        // Carregar os horários do barbeiro e criar a tabela
        viewModel.getBarberSchedules(barberId) { scheduleMap ->
            createScheduleTable(scheduleMap)
        }

        // Botão para voltar à página inicial
        binding.btnBackScheduleBackToHome.setOnClickListener {
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }

        // Botão para salvar os horários
        binding.btnSaveSchedule.setOnClickListener {
            saveBarberSchedule(barberId)
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }
    }

    private fun createScheduleTable(scheduleMap: Map<Int, List<Int>>) {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val hours = listOf(
            "9h", "10h", "11h", "12h",
            "13h", "14h", "15h", "16h",
            "17h", "18h"
        )

        val tableLayout = binding.tableLayoutSchedule
        tableLayout.removeAllViews() // Limpar a tabela antes de recriá-la

        val backgroundColors = listOf("#FAFAFA", "#EEEEEE")

        // Cabeçalho
        val headerRow = TableRow(requireContext())
        for ((index, day) in daysOfWeek.withIndex()) {
            val dayCell = TextView(requireContext()).apply {
                text = day
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundColor(Color.parseColor(backgroundColors[index % 2]))
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }
            headerRow.addView(dayCell)
        }
        tableLayout.addView(headerRow)

        // Linhas de horários com botões
        for (hourIndex in hours.indices) {
            val row = TableRow(requireContext())
            for ((dayIndex, day) in daysOfWeek.withIndex()) {
                val button = Button(requireContext()).apply {
                    text = hours[hourIndex]
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    setBackgroundColor(Color.parseColor(backgroundColors[dayIndex % 2]))
                    setTextColor(Color.BLACK)
                    isAllCaps = false

                    // Marcar se estiver salvo no banco
                    val hour = hourIndex + 9
                    if (scheduleMap[dayIndex]?.contains(hour) == true) {
                        setBackgroundColor(Color.parseColor("#FF9800"))
                        setTextColor(Color.WHITE)
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

    private fun toggleButtonState(button: Button, originalColor: String) {
        if (button.tag == "selected") {
            button.tag = "unselected"
            button.setBackgroundColor(Color.parseColor(originalColor))
            button.setTextColor(Color.BLACK)
        } else {
            button.tag = "selected"
            button.setBackgroundColor(Color.parseColor("#FF9800"))
            button.setTextColor(Color.WHITE)
        }
    }

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
