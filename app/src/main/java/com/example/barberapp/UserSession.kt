package com.example.barberapp

import android.util.Log
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client

/**
 * User session
 * Global variable that saves the user information
 *
 * @constructor Create empty User session
 */
object UserSession {
    var loggedInBarber: Barber? = null
    var loggedInClient: Client? = null
    var isKeepLoggedIn: Boolean = false // Variável global para armazenar o estado do checkbox


    // selected IDs to make an appointment
    var selectedBarberShopId: Int? = null
    var selectedBarberId: Int? = null
    var selectedServiceIds = mutableListOf<Int>()
    var selectedAppointmentTime: String? = null
    var selectedAppointmentDate: String? = null

    val isLoggedInAsBarber: Boolean
        get() = loggedInBarber != null

    /**
     * Clear session
     *
     */
    fun clearSession() {
        loggedInClient = null
        loggedInBarber = null
        selectedBarberShopId = null
        selectedBarberId = null
        selectedServiceIds.clear()
        selectedAppointmentTime = null
        selectedAppointmentTime = null

        Log.d("ClearSession","UserSession: Session cleared.")
        Log.d("ClearSession","Current state: BarberShopId=$selectedBarberShopId, BarberId=$loggedInBarber, Services=$selectedServiceIds")
    }

}
