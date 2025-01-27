package com.example.barberapp.MyAppointmentsBarber

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.AppointmentDetails
import kotlinx.coroutines.launch

class BarberAppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _appointments = MutableLiveData<List<AppointmentDetails>>()
    val appointments: LiveData<List<AppointmentDetails>> get() = _appointments

    /**
     * Load appointment information based on the clientId
     *
     * @param barberId
     */
    fun loadAppointments(barberId: Int) {
        viewModelScope.launch {
            try {
                val details = appointmentDao.getAppointmentsForBarber(barberId)
                _appointments.postValue(details)
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error while loading the appointment: ${e.message}")
            }
        }
    }

}