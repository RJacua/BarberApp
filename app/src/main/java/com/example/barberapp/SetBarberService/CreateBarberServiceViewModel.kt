package com.example.barberapp.Register

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.BarberService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time

class CreateBarberServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    private val _service = MutableLiveData<BarberService?>()
    val service: LiveData<BarberService?> get() = _service

    private val _serviceName = MutableLiveData<String?>()
    val serviceName: LiveData<String?> get() = _serviceName


    /**
     * Load service name from the Service Database
     *
     * @param serviceId
     */
    fun loadServiceName(serviceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(
                "CreateBarberServiceVM",
                "Carregando o nome do serviço para serviceId: $serviceId"
            )
            val service = database.serviceDao().getServiceById(serviceId)
            if (service != null) {
                Log.d("CreateBarberServiceVM", "Loaded service's name: ${service.name}")
                _serviceName.postValue(service.name)
            } else {
                Log.e("CreateBarberServiceVM", "Service not sound for serviceId: $serviceId")
                _serviceName.postValue("Unknown Service")
            }
        }
    }

    /**
     * Load service information (price and duration) from the BarberService Database
     *
     * @param barberId
     * @param serviceId
     */
    fun loadService(barberId: Int, serviceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(
                "CreateBarberServiceVM",
                "Loading service info to barberId: $barberId, serviceId: $serviceId"
            )
            val barberService =
                database.barberserviceDao().getBarberServiceById(barberId, serviceId)
            if (barberService != null) {
                Log.d("CreateBarberServiceVM", "Service loaded: $barberService")
                _service.postValue(barberService)
            } else {
                Log.e(
                    "CreateBarberServiceVM",
                    "No service found for barberId: $barberId and serviceId: $serviceId"
                )
                _service.postValue(null)
            }
        }
    }

    /**
     * Update barber service
     *
     * @param barberId
     * @param serviceId
     * @param duration
     * @param price
     * @param isActive
     */
    fun updateBarberService(
        barberId: Int,
        serviceId: Int,
        duration: Time,
        price: Double,
        isActive: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingService =
                database.barberserviceDao().getBarberServiceById(barberId, serviceId)
            if (existingService != null) {
                Log.d("CreateBarberServiceVM", "Service found: $existingService")

                val updatedService = existingService.copy(
                    duration = duration,
                    price = price,
                    isActive = isActive
                )

                database.barberserviceDao().update(updatedService)
                Log.d("CreateBarberServiceVM", "Service updated!")

                val updatedServiceFromDb =
                    database.barberserviceDao().getBarberServiceById(barberId, serviceId)
                Log.d("CreateBarberServiceVM", "Service updated in the database: $updatedServiceFromDb")
            } else {
                Log.e(
                    "CreateBarberServiceVM",
                    "Service not found for barberId=$barberId and serviceId=$serviceId"
                )
            }
        }
    }
}
