package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.BarberService
import com.example.barberapp.data.Barbershop
import com.example.barberapp.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    // Obter todas as barbearias como LiveData
    val barbershops: LiveData<List<Barbershop>> = database.barbershopDao().getAllBarbershops()

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
            logAllClients()

            // Limpa a sessão antes de logar
            UserSession.clearSession()

            // Realizar login automaticamente após o registro
            val client = database.clientDao().getAllClients().find { it.email == email && it.password == password }
            if (client != null) {
                UserSession.loggedInClient = client // Atualiza o UserSession com o novo cliente
            }

            withContext(Dispatchers.Main) {
                callback(client != null) // Sucesso se o cliente foi encontrado
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
            logAllBarbers()

            // Limpa a sessão antes de logar
            UserSession.clearSession()

            // Criar os serviços default e realizar login
            val barber = database.barberDao().getAllBarbersList()
                .find { it.email == email && it.password == password }
            if (barber != null) {
                createDefaultServicesForBarber(barber.barberId)
                UserSession.loggedInBarber = barber // Atualiza o UserSession com o novo barbeiro
            }

            withContext(Dispatchers.Main) {
                callback(barber != null) // Sucesso se o barbeiro foi encontrado
            }
        }
    }

    // Criar serviços padrão
     fun createDefaultServicesForBarber(barberId: Int) {
        val services = listOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 // IDs dos serviços
        )

        val barberServices = services.map { serviceId ->
            BarberService(
                barberId = barberId,
                serviceId = serviceId,
                duration = Time.valueOf("00:00:00"), // Duração padrão como null
                price = 0.0, // Preço padrão como 0.0
                isActive = false // Inativo por padrão
            )
        }

        database.barberserviceDao().insertAll(barberServices)
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
            val barbers = database.barberDao().getAllBarbersList()
            barbers.forEach {
                Log.d(
                    "RegisterViewModel",
                    "Barber: ${it.name}, Email: ${it.email}, id: ${it.barberId}, shopid: ${it.barbershopId}, pass: ${it.password}"
                )
            }
        }
    }
}


