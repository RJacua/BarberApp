package com.example.barberapp.UtilityClasses

/**
 * Appointment details
 *
 * @property appointmentId
 * @property date
 * @property time
 * @property status
 * @property barbershopName
 * @property barberName
 * @property serviceName
 * @property price
 * @property clientName
 * @constructor Create empty Appointment details
 */
data class AppointmentDetails(
    val appointmentId: Int,
    val date: String,
    val time: String,
    val status: String,
    val barbershopName: String,
    val barberName: String,
    val serviceName: String,
    val price: Double,
    val clientName: String?,
)
