package com.example.barberapp.BarberSchedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.BarberSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase(application)

    /**
     * Salva os horários do barbeiro na base de dados.
     * Substitui todos os horários existentes por novos.
     */
    fun saveBarberSchedule(barberId: Int, scheduleMap: Map<Int, List<Int>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val barberScheduleDao = database.barberscheduleDao()

            // Apagar todos os horários existentes para o barbeiro
            barberScheduleDao.deleteSchedulesForBarber(barberId)

            // Inserir os novos horários
            scheduleMap.forEach { (dayOfWeek, hoursList) ->
                val hoursString = hoursList.joinToString(",") // Converter a lista para String
                val barberSchedule = BarberSchedule(
                    barberId = barberId,
                    dayOfWeek = dayOfWeek,
                    hours = hoursString
                )
                barberScheduleDao.insert(barberSchedule)
            }
        }
    }

    /**
     * Recupera os horários do barbeiro.
     */
    fun getBarberSchedules(barberId: Int, callback: (Map<Int, List<Int>>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedules = database.barberscheduleDao().getSchedulesForBarber(barberId)
            val scheduleMap = schedules.associate { schedule ->
                schedule.dayOfWeek to schedule.hours.split(",").map { it.toInt() }
            }
            // Garanta que o callback seja executado na thread principal
            withContext(Dispatchers.Main) {
                callback(scheduleMap)
            }
        }
    }


}