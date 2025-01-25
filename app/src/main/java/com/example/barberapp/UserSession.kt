package com.example.barberapp

import android.util.Log
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client
import com.example.barberapp.data.Service

object UserSession {
    var loggedInBarber: Barber? = null
    var loggedInClient: Client? = null
    var isKeepLoggedIn: Boolean = false // Variável global para armazenar o estado do checkbox


    // IDs selecionados
    var selectedBarberShopId: Int? = null
    var selectedBarberId: Int? = null
    var selectedServiceIds = mutableListOf<Int>()
    var selectedAppointmentTime: String? = null
    var selectedAppointmentDate: String? = null


    // Gerenciar seleção de serviços
    fun addService(serviceId: Int) {
        if (!selectedServiceIds.contains(serviceId)) {
            selectedServiceIds.add(serviceId)
            logSession("Service $serviceId added.")
        }
    }

    fun removeService(serviceId: Int) {
        if (selectedServiceIds.contains(serviceId)) {
            selectedServiceIds.remove(serviceId)
            logSession("Service $serviceId removed.")
        }
    }

    fun getSelectedServices(): List<Int> {
        return selectedServiceIds.toList()
    }


    fun clearSession() {
        loggedInClient = null
        loggedInBarber = null
        selectedBarberShopId = null
        selectedBarberId = null
        selectedServiceIds.clear()
        selectedAppointmentTime = null
        selectedAppointmentTime = null

        // Para garantir a limpeza, log tudo após o reset
        Log.d("ClearSession","UserSession: Session cleared.")
        Log.d("ClearSession","Current state: BarberShopId=$selectedBarberShopId, BarberId=$loggedInBarber, Services=$selectedServiceIds")
    }

    // Log para acompanhar o estado
    fun logSession(message: String) {

        Log.d("ClearSession","UserSession: $message")
        Log.d("ClearSession","Current state: BarberShopId=$selectedBarberShopId, BarberId=$selectedBarberId, Services=$selectedServiceIds")

    }
}
