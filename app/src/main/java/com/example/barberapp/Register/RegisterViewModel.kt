package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    fun registerClient(name: String, email: String, password: String, phone: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            database.clientDao().insert(
                Client(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone
                )
            )
            logAllClients() // console log todos os clientes após a inserção
        }
    }

    fun registerBarber(name: String, email: String, password: String, bio: String = "Novo barbeiro", barbershopId: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            database.barberDao().insert(
                Barber(
                    name = name,
                    email = email,
                    password = password,
                    bio = bio,
                    barbershopId = barbershopId
                )
            )
        }
    }

    fun logAllClients() {
        viewModelScope.launch(Dispatchers.IO) {
            val clients = database.clientDao().getAllClients()
            Log.d("RegisterViewModel", "Clientes registrados: ${clients.size}")
            clients.forEach { client ->
                Log.d("RegisterViewModel", "Cliente: $client")
            }
        }
    }

}
