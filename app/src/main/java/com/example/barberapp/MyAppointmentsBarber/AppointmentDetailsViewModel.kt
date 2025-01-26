package com.example.barberapp.MyAppointmentsBarber

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.AppointmentDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppointmentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _appointment = MutableLiveData<AppointmentDetails?>()
    val appointment: LiveData<AppointmentDetails?> get() = _appointment

    // Carregar os dados do appointment
    fun loadAppointment(appointmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appointment = appointmentDao.getAppointmentDetailsById(appointmentId)
                _appointment.postValue(appointment)
                Log.d("AppointmentDetailsVM", "Appointment carregado: $appointment")
            } catch (e: Exception) {
                Log.e("AppointmentDetailsVM", "Erro ao carregar appointment: ${e.message}")
            }
        }
    }

    // Atualizar o status do appointment
    fun updateAppointmentStatus(appointmentId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appointmentDao.updateAppointmentStatus(appointmentId, status)
                Log.d("AppointmentDetailsVM", "Status atualizado para: $status")
                try {
                    val appointment = appointmentDao.getAppointmentDetailsById(appointmentId)
                    _appointment.postValue(appointment)
                    Log.d("AppointmentDetailsVM", "Appointment carregado: $appointment")
                } catch (e: Exception) {
                    Log.e("AppointmentDetailsVM", "Erro ao carregar appointment: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("AppointmentDetailsVM", "Erro ao atualizar status: ${e.message}")
            }
        }
    }
}
