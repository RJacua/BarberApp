package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())
    private val loginViewModel = LoginViewModel(application) // Instância do LoginViewModel

    fun registerClient(
        name: String,
        email: String,
        password: String,
        phone: String = "",
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            database.clientDao().insert(
                Client(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone
                )
            )
            logAllClients() // Console log todos os clientes após a inserção

            // Realizar login automaticamente após o registro
            val loginResult = loginViewModel.login(email, password)
            withContext(Dispatchers.Main) {
                callback(loginResult != null) // Sucesso se loginResult não for null
            }
        }
    }

    fun registerBarber(
        name: String,
        email: String,
        password: String,
        bio: String,
        barbershopId: Int,
        callback: (Boolean) -> Unit
    ) {
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

            logAllBarbers() // Console log todos os clientes após a inserção

            // Realizar login automaticamente após o registro
            val loginResult = loginViewModel.login(email, password)
            withContext(Dispatchers.Main) {
                callback(loginResult != null) // Sucesso se loginResult não for null
            }
        }
    }

    private fun logAllClients() {
        viewModelScope.launch(Dispatchers.IO) {
            val clients = database.clientDao().getAllClients()
            clients.forEach {
                Log.d("RegisterViewModel", "Cliente: ${it.name}, Email: ${it.email}")
            }
        }
    }

    private fun logAllBarbers() {
        viewModelScope.launch(Dispatchers.IO) {
            val barber = database.barberDao().getAllBarbers()
            barber.forEach {
                Log.d("RegisterViewModel", "Barber: ${it.name}, Email: ${it.email}, id: ${it.barberId}, shopid: ${it.barbershopId}, pass: ${it.password}")
            }
        }
    }
}

