package com.example.barberapp.data

import java.sql.Time

data class AppointmentWithDuration(
    val time: String, // Assuma formato "HH:mm"
    val duration: Time // Duração em minutos
)
