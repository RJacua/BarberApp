package com.example.barberapp.data

data class AppointmentDetails(
    val appointmentId: Int,
    val date: String,
    val time: String,
    val status: String,
    val barbershopName: String,
    val barberName: String,
    val serviceName: String,
    val price: Double
)
