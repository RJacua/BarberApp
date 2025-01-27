package com.example.barberapp.SetBarberSchedule

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
     * Save the barber schedules in the data base. Always replaces the existing schedule.
     * @param barberId
     * @param scheduleMap
     */
    fun saveBarberSchedule(barberId: Int, scheduleMap: Map<Int, List<Int>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val barberScheduleDao = database.barberscheduleDao()

            // Erase the whole schedule to avoid conflicts, lazy but effective
            barberScheduleDao.deleteSchedulesForBarber(barberId)

            // Insert the new schedule
            scheduleMap.forEach { (dayOfWeek, hoursList) ->
                val hoursString = hoursList.joinToString(",") // Converts the list to String
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
     * Get barber schedules
     *
     * @param barberId
     * @param callback
     * @receiver
     */
    fun getBarberSchedules(barberId: Int, callback: (Map<Int, List<Int>>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedules = database.barberscheduleDao().getSchedulesForBarber(barberId)
            val scheduleMap = schedules.associate { schedule ->
                schedule.dayOfWeek to schedule.hours.split(",").map { it.toInt() }
            }
            // Guarantees that the callback will be executed in the main thread
            withContext(Dispatchers.Main) {
                callback(scheduleMap)
            }
        }
    }


}