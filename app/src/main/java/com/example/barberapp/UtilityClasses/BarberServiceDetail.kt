package com.example.barberapp.UtilityClasses

import java.sql.Time

data class BarberServiceDetail(
    val serviceId: Int,
    val name: String,
    val description: String,
    val price: Double?,
    val duration: Time?,
    val isActive: Boolean
)
