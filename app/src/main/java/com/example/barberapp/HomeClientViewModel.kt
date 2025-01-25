package com.example.barberapp

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Appointment

class HomeClientViewModel(application: Application) : AndroidViewModel(application) {

    private val barberserviceDao = AppDatabase(application).barberserviceDao()
    private val appointmentDao = AppDatabase(application).appointmentDao()

    suspend fun createAppointments() {
        val client = UserSession.loggedInClient
        val barberId = UserSession.selectedBarberId
        val selectedServices = UserSession.selectedServiceIds
        val appointmentDate = UserSession.selectedAppointmentDate
        var appointmentTime = UserSession.selectedAppointmentTime

        if (client == null || barberId == null || appointmentDate == null || appointmentTime == null || selectedServices.isEmpty()) {
            Log.e("CreateAppointment", "Informações incompletas para criar a marcação.")
            return
        }

        // Itera pelos serviços selecionados e cria um Appointment para cada um
        for (serviceId in selectedServices) {
            // Obtém o BarberService correspondente
            val barberService = barberserviceDao.getBarberServiceById(barberId, serviceId)
            if (barberService == null) {
                Log.e("CreateAppointment", "Serviço não encontrado para BarberID: $barberId e ServiceID: $serviceId")
                continue
            }

            // Cria o objeto Appointment
            val appointment = Appointment(
                clientId = client.clientId,
                barberServiceId = barberService.barberServiceId,
                date = appointmentDate,
                time = appointmentTime!!,
                status = "Ativo"
            )

            // Insere o Appointment na base de dados
            appointmentDao.insert(appointment)

            // Calcula o tempo do próximo serviço
            val serviceDuration = barberService.duration.toString() // "HH:MM:SS"
            val durationParts = serviceDuration.split(":").map { it.toInt() }
            val hours = durationParts[0]
            val minutes = durationParts[1]

            // Atualiza a hora do próximo serviço
            val timeParts = appointmentTime.split(":").map { it.toInt() }
            val newHour = timeParts[0] + hours + (timeParts[1] + minutes) / 60
            val newMinute = (timeParts[1] + minutes) % 60
            appointmentTime = String.format("%02d:%02d", newHour, newMinute)
        }

        UserSession.selectedAppointmentDate = null
        UserSession.selectedAppointmentTime = null
        Log.d("CreateAppointment", "Marcações criadas com sucesso para o cliente ${client.clientId}.")
    }

}