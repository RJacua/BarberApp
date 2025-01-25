package com.example.barberapp.data

import java.sql.Time

data class BarberServiceDetail(
    val serviceId: Int,
    val name: String,
    val description: String,
    val price: Double?,
    val duration: Time?
)
