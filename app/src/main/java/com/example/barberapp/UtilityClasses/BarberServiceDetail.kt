package com.example.barberapp.UtilityClasses

import java.sql.Time

/**
 * Barber service detail
 *
 * @property serviceId
 * @property name
 * @property description
 * @property price
 * @property duration
 * @property isActive
 * @constructor Create empty Barber service detail
 */
data class BarberServiceDetail(
    val serviceId: Int,
    val name: String,
    val description: String,
    val price: Double?,
    val duration: Time?,
    val isActive: Boolean
)
