package com.example.barberapp

import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client

object UserSession {
    var loggedInBarber: Barber? = null
    var loggedInClient: Client? = null

    fun clearSession() {
        loggedInBarber = null
        loggedInClient = null
    }
}
