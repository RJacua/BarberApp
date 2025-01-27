package com.example.barberapp.UtilityClasses

import java.sql.Time

/**
 * Appointment with duration
 *
 * @property time
 * @property duration
 * @constructor Create empty Appointment with duration
 */
data class AppointmentWithDuration(
    val time: String, // Assuma formato "HH:mm"
    val duration: Time // Duração em minutos
)
