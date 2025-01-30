package com.example.barberapp.MyAppointmentsClient

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

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppDatabase(application).appointmentDao()

    private val _appointments = MutableLiveData<List<AppointmentDetails>>()
    val appointments: LiveData<List<AppointmentDetails>> get() = _appointments

    private val _appointment = MutableLiveData<AppointmentDetails?>()
    val appointment: LiveData<AppointmentDetails?> get() = _appointment

    /**
     * Load appointments to Live Data
     *
     * @param clientId
     */
    fun loadAppointments(clientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val details = appointmentDao.getAppointmentDetailsForClient(clientId)
                if (_appointments.value != details) {
                    _appointments.postValue(details)
                }
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Erro ao carregar marcações: ${e.message}")
            }
        }
    }

    /**
     * Cancel client appointment
     *
     * @param appointmentId
     */
    fun cancelAppointment(appointmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // update appointment status on the data base
                appointmentDao.updateAppointmentStatus(appointmentId, "Canceled")

                // update list on LiveData
                val updatedList = _appointments.value?.map {
                    if (it.appointmentId == appointmentId) it.copy(status = "Canceled") else it
                }
                updatedList?.let {
                    _appointments.postValue(it)
                }
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Erro ao cancelar marcação: ${e.message}")
            }
        }
    }

}