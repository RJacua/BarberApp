package com.example.barberapp.Login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.barberapp.data.AppDatabase

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase(application)

    /**
     * Verifica as credenciais de login.
     * Retorna:
     * - "client" se o usuário for um cliente.
     * - "barber" se o usuário for um barbeiro.
     * - null se o login falhar.
     */
    suspend fun login(email: String, password: String): String? {
        val client = database.clientDao().getAllClients()
            .find { it.email == email && it.password == password }
        if (client != null) return "client"

        val barber = database.barberDao().getAllBarbers()
            .find { it.email == email && it.password == password }
        if (barber != null) return "barber"

        return null
    }
}