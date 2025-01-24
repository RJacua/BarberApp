package com.example.barberapp

import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client

object UserSession {
    var loggedInBarber: Barber? = null
    var loggedInClient: Client? = null

    // IDs selecionados
    var selectedBarberShopId: Int? = null
    var selectedBarberId: Int? = null
    private val selectedServiceIds = mutableListOf<Int>()

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
        loggedInBarber = null
        loggedInClient = null
        selectedBarberShopId = null
        selectedBarberId = null
        selectedServiceIds.clear()
        logSession("Session cleared.")
    }

    // Log para acompanhar o estado
    fun logSession(message: String) {
        println("UserSession: $message")
        println("Current state: BarberShopId=$selectedBarberShopId, BarberId=$selectedBarberId, Services=$selectedServiceIds")
    }
}
