package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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

    val barbershops: LiveData<List<Barbershop>> = database.barbershopDao().getAllBarbershops()

    /**
     * Register client
     *
     * @param name
     * @param email
     * @param password
     * @param phone
     * @param callback
     * @receiver
     */
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

            // clean session before login
            UserSession.clearSession()

            // automatic login after afet sign up
            val client = database.clientDao().getAllClients().find { it.email == email && it.password == password }
            if (client != null) {
                UserSession.loggedInClient = client // update UserSession with the new client info
            }

            withContext(Dispatchers.Main) {
                callback(client != null) // success if new client is found
            }
        }
    }

    /**
     * Register barber
     *
     * @param name
     * @param email
     * @param password
     * @param bio
     * @param barbershopId
     * @param callback
     * @receiver
     */
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

            // clean session before login
            UserSession.clearSession()

            // create default services and login
            val barber = database.barberDao().getAllBarbersList()
                .find { it.email == email && it.password == password }
            if (barber != null) {
                UserSession.loggedInBarber = barber // update UserSession with new barber info
            }

            withContext(Dispatchers.Main) {
                callback(barber != null) // success if barber is found
            }
        }
    }

    /**
     * Create default services for barber
     *
     * @param barberId
     */
     fun createDefaultServicesForBarber(barberId: Int) {
        val services = listOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 // IDs dos serviços
        )

        val barberServices = services.map { serviceId ->
            BarberService(
                barberId = barberId,
                serviceId = serviceId,
                duration = Time.valueOf("00:00:00"), // default duration is null
                price = 0.0, // defaul price is null
                isActive = false // default status as not active
            )
        }

        database.barberserviceDao().insertAll(barberServices)
    }

    /**
     * Log all clients
     *
     */
    private fun logAllClients() {
        viewModelScope.launch(Dispatchers.IO) {
            val clients = database.clientDao().getAllClients()
            clients.forEach {
                Log.d("RegisterViewModel", "Cliente: ${it.name}, Email: ${it.email}")
            }
        }
    }

    /**
     * Log all barbers
     *
     */
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


