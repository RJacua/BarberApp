package com.example.barberapp

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

        // Para garantir a limpeza, log tudo após o reset
        println("UserSession: Session cleared.")
        println("Current state: BarberShopId=$selectedBarberShopId, BarberId=$loggedInBarber, Services=$selectedServiceIds")
    }

    // Log para acompanhar o estado
    fun logSession(message: String) {
        println("UserSession: $message")
        println("Current state: BarberShopId=$selectedBarberShopId, BarberId=$selectedBarberId, Services=$selectedServiceIds")
    }
}
