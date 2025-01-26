package com.example.barberapp

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Appointment
import com.example.barberapp.data.AppointmentDetails
import com.example.barberapp.data.AppointmentWithDuration
import com.example.barberapp.data.BarberServiceDetail
import com.example.barberapp.data.Barbershop
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

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