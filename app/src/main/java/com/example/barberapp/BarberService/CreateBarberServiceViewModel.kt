package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberapp.BarberService.CreateBarberServiceFragment
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.BarberService
import com.example.barberapp.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time

class CreateBarberServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    fun registerBarberService(
        barberId: Int,
        serviceId: Int,
        duration: Time,
        price: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            database.barberserviceDao().insert(
                BarberService(
                    barberId = barberId,
                    serviceId = serviceId,
                    duration = duration,
                    price = price
                )
            )
            logAllBarberServices() // Console log todos os clientes após a inserção

        }
    }

    private fun logAllBarberServices() {
        viewModelScope.launch(Dispatchers.IO) {
            val barberServices = database.barberserviceDao().getAllBarberServices()
            barberServices.forEach {
                Log.d("RegisterBarberService", "Id: ${it.barberServiceId}, BarberId: ${it.barberId}, ServiceId: ${it.serviceId}, Price: ${it.price}, Duration: ${it.duration}")
            }
        }
    }
}

