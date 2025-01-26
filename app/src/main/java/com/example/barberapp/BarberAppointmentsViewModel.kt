package com.example.barberapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.AppointmentDetails
import kotlinx.coroutines.launch

class BarberAppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _appointments = MutableLiveData<List<AppointmentDetails>>()
    val appointments: LiveData<List<AppointmentDetails>> get() = _appointments

    fun loadAppointments(clientId: Int) {
        viewModelScope.launch {
            try {
                val details = appointmentDao.getAppointmentDetailsForClient(clientId)
                _appointments.postValue(details)
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Erro ao carregar marcações: ${e.message}")
            }
        }
    }

}