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

class AppointmentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _appointment = MutableLiveData<AppointmentDetails?>()
    val appointment: LiveData<AppointmentDetails?> get() = _appointment


    /**
     * Load appointment information
     *
     * @param appointmentId
     */
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


    /**
     * Changes the appointment status based on the dropdown selection
     *
     * @param appointmentId
     * @param status
     */
    fun updateAppointmentStatus(appointmentId: Int, status: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                appointmentDao.updateAppointmentStatus(appointmentId, status)
                Log.d("AppointmentDetailsVM", "Status atualizado para: $status")

                // Em vez de duplicar a lógica, chamamos loadAppointment() para atualizar os dados
                loadAppointment(appointmentId)

            } catch (e: Exception) {
                Log.e("AppointmentDetailsVM", "Erro ao atualizar status: ${e.message}")
                e.printStackTrace()
            }
        }

//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                appointmentDao.updateAppointmentStatus(appointmentId, status)
//                Log.d("AppointmentDetailsVM", "Status atualizado para: $status")
//                try {
//                    val appointment = appointmentDao.getAppointmentDetailsById(appointmentId)
//                    _appointment.postValue(appointment)
//                    Log.d("AppointmentDetailsVM", "Appointment carregado: $appointment")
//                } catch (e: Exception) {
//                    Log.e("AppointmentDetailsVM", "Erro ao carregar appointment: ${e.message}")
//                }
//            } catch (e: Exception) {
//                Log.e("AppointmentDetailsVM", "Erro ao atualizar status: ${e.message}")
//            }
//        }

    }
}
