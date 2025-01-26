package com.example.barberapp.Home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Appointment

class HomeClientViewModel(application: Application) : AndroidViewModel(application) {

    private val barberserviceDao = AppDatabase(application).barberserviceDao()
    private val appointmentDao = AppDatabase(application).appointmentDao()

    suspend fun createAppointments(): Result<Boolean> {
        val client = UserSession.loggedInClient
        val barberId = UserSession.selectedBarberId
        val selectedServices = UserSession.selectedServiceIds
        val appointmentDate = UserSession.selectedAppointmentDate
        var appointmentTime = UserSession.selectedAppointmentTime

        if (client == null || barberId == null || appointmentDate == null || appointmentTime == null || selectedServices.isEmpty()) {
            return Result.failure(Exception("Informações incompletas para criar a marcação."))
        }

        try {
            // Itera pelos serviços selecionados e cria um Appointment para cada um
            for (serviceId in selectedServices) {
                val barberService = barberserviceDao.getBarberServiceById(barberId, serviceId)
                if (barberService == null) {
                    return Result.failure(Exception("Serviço não encontrado para BarberID: $barberId e ServiceID: $serviceId"))
                }

                val appointment = Appointment(
                    clientId = client.clientId,
                    barberServiceId = barberService.barberServiceId,
                    date = appointmentDate,
                    time = appointmentTime!!,
                    status = "Ativo"
                )

                appointmentDao.insert(appointment)

                // Calcula a hora do próximo serviço
                val serviceDuration = barberService.duration.toString() // "HH:MM:SS"
                val durationParts = serviceDuration.split(":").map { it.toInt() }
                val hours = durationParts[0]
                val minutes = durationParts[1]

                val timeParts = appointmentTime.split(":").map { it.toInt() }
                val newHour = timeParts[0] + hours + (timeParts[1] + minutes) / 60
                val newMinute = (timeParts[1] + minutes) % 60
                appointmentTime = String.format("%02d:%02d", newHour, newMinute)
            }

            UserSession.selectedAppointmentDate = null
            UserSession.selectedAppointmentTime = null
            return Result.success(true)
        } catch (e: Exception) {
            Log.e("CreateAppointment", "Erro ao criar marcações: ${e.message}")
            return Result.failure(e)
        }
    }


}