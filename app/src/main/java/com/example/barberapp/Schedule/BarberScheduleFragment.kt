package com.example.barberapp.Schedule

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentBarberScheduleBinding

class BarberScheduleFragment : Fragment() {

    private lateinit var binding: FragmentBarberScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberScheduleBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar a tabela programaticamente
        createScheduleTable()

        super.onViewCreated(view, savedInstanceState)

        binding.btnBackScheduleBackToHome.setOnClickListener {
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }
    }

    private fun createScheduleTable() {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val hours = listOf(
            "9h-10h", "10h-11h", "11h-12h", "12h-13h",
            "13h-14h", "14h-15h", "15h-16h", "16h-17h",
            "17h-18h", "18h-19h"
        )

        val tableLayout = binding.tableLayoutSchedule

        // Cores alternadas para as colunas
        val backgroundColors = listOf("#FAFAFA", "#EEEEEE") // Cores alternadas

        // Cabeçalho da tabela
        val headerRow = TableRow(requireContext())
        val headerCell = TextView(requireContext()).apply {
            text = "Hours"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(16, 8, 16, 8)
            setBackgroundColor(Color.parseColor("#CCCCCC")) // Cor fixa para o cabeçalho
        }
        headerRow.addView(headerCell)

        for ((index, day) in daysOfWeek.withIndex()) {
            val dayCell = TextView(requireContext()).apply {
                text = day
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundColor(Color.parseColor(backgroundColors[index % 2])) // Alterna as cores
            }
            headerRow.addView(dayCell)
        }
        tableLayout.addView(headerRow)

        // Linhas de horários
        for (hour in hours) {
            val row = TableRow(requireContext())

            // Primeira coluna (horário)
            val hourCell = TextView(requireContext()).apply {
                text = hour
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundColor(Color.parseColor("#FAFAFA")) // Cor fixa para os horários
            }
            row.addView(hourCell)

            // Colunas com CheckBox para cada dia
            for ((index, day) in daysOfWeek.withIndex()) {
                val checkBox = CheckBox(requireContext()).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL // Centraliza horizontalmente
                    }
                    gravity = Gravity.CENTER // Centraliza dentro do CheckBox
                    scaleX = 1.5f // Ajusta o tamanho
                    scaleY = 1.5f // Ajusta o tamanho
                    setBackgroundColor(Color.parseColor(backgroundColors[index % 2])) // Alterna as cores
                }
                row.addView(checkBox)
            }
            tableLayout.addView(row)
        }
    }


    private fun TextView.textStyle() {
        textSize = 14f
        setPadding(16, 8, 16, 8)
        gravity = Gravity.CENTER
        setBackgroundColor(Color.parseColor("#EEEEEE"))
    }
}