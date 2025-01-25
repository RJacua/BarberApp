package com.example.barberapp.ChooseAppointment

import android.app.Application
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.BarberSchedule
import com.example.barberapp.data.Service
import com.example.barberapp.databinding.FragmentBarberShopBinding

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleDao = AppDatabase(application).barberscheduleDao()

    private val _morningSlots = MutableLiveData<List<String>>()
    val morningSlots: LiveData<List<String>> = _morningSlots

    private val _afternoonSlots = MutableLiveData<List<String>>()
    val afternoonSlots: LiveData<List<String>> = _afternoonSlots

    fun loadSchedules(barberId: Int, dayOfWeek: Int) {
        val schedules = scheduleDao.getSchedulesByDay(barberId, dayOfWeek)
        Log.d("Horario", "Horários encontrados: $schedules")
        if (schedules.isNotEmpty()) {
            val hours = schedules.first().hours
            Log.d("Horario", "Horários encontrados: $hours")
            val (morning, afternoon) = processHours(hours)
            _morningSlots.value = morning
            _afternoonSlots.value = afternoon
            Log.d("Horario", "Manhã: $morning, Tarde: $afternoon")
        }
    }

    private fun processHours(hours: String): Pair<List<String>, List<String>> {
        val hourList = hours.split(",").map { it.toInt() }
        val morning = mutableListOf<String>()
        val afternoon = mutableListOf<String>()

        for (hour in hourList) {
            if (hour in 9..13) {
                morning.addAll(generateSlots(hour))
            } else if (hour in 14..18) {
                afternoon.addAll(generateSlots(hour))
            }
        }
        return Pair(morning, afternoon)
    }

    private fun generateSlots(hour: Int): List<String> {
        val slots = mutableListOf<String>()
        val minutes = listOf("00", "15", "30", "45")
        for (minute in minutes) {
            slots.add("$hour:$minute")
        }
        return slots
    }
}
