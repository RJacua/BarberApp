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

    // LiveData para observar o serviço atual
    private val _service = MutableLiveData<BarberService?>()
    val service: LiveData<BarberService?> get() = _service

    private val _serviceName = MutableLiveData<String?>()
    val serviceName: LiveData<String?> get() = _serviceName

    /**
     * Carrega o nome do serviço baseado no `serviceId`.
     */
    fun loadServiceName(serviceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(
                "CreateBarberServiceVM",
                "Carregando o nome do serviço para serviceId: $serviceId"
            )
            val service = database.serviceDao().getServiceById(serviceId)
            if (service != null) {
                Log.d("CreateBarberServiceVM", "Nome do serviço carregado: ${service.name}")
                _serviceName.postValue(service.name)
            } else {
                Log.e("CreateBarberServiceVM", "Serviço não encontrado para serviceId: $serviceId")
                _serviceName.postValue("Unknown Service")
            }
        }
    }

    /**
     * Carrega as informações de um `BarberService` específico.
     */
    fun loadService(barberId: Int, serviceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(
                "CreateBarberServiceVM",
                "Carregando serviço para barberId: $barberId, serviceId: $serviceId"
            )
            val barberService =
                database.barberserviceDao().getBarberServiceById(barberId, serviceId)
            if (barberService != null) {
                Log.d("CreateBarberServiceVM", "Serviço carregado: $barberService")
                _service.postValue(barberService)
            } else {
                Log.e(
                    "CreateBarberServiceVM",
                    "Nenhum serviço encontrado para barberId: $barberId, serviceId: $serviceId"
                )
                _service.postValue(null)
            }
        }
    }

    /**
     * Atualiza as informações de um serviço existente.
     */
    fun updateBarberService(
        barberId: Int,
        serviceId: Int,
        duration: Time,
        price: Double,
        isActive: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Carregar o serviço existente
            val existingService =
                database.barberserviceDao().getBarberServiceById(barberId, serviceId)
            if (existingService != null) {
                Log.d("CreateBarberServiceVM", "Serviço existente encontrado: $existingService")

                // Atualizar os campos necessários
                val updatedService = existingService.copy(
                    duration = duration,
                    price = price,
                    isActive = isActive
                )

                // Atualizar no banco de dados
                database.barberserviceDao().update(updatedService)
                Log.d("CreateBarberServiceVM", "Serviço atualizado com sucesso!")

                // Log do serviço atualizado diretamente do banco
                val updatedServiceFromDb =
                    database.barberserviceDao().getBarberServiceById(barberId, serviceId)
                Log.d("CreateBarberServiceVM", "Serviço atualizado no banco: $updatedServiceFromDb")
            } else {
                Log.e(
                    "CreateBarberServiceVM",
                    "Serviço não encontrado para barberId=$barberId e serviceId=$serviceId"
                )
            }
        }
    }


    /**
     * Insere um novo serviço no banco de dados.
     */
    fun registerBarberService(
        barberId: Int,
        serviceId: Int,
        duration: Time,
        price: Double,
        isActive: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newService = BarberService(
                barberId = barberId,
                serviceId = serviceId,
                duration = duration,
                price = price,
                isActive = isActive
            )
            Log.d("CreateBarberServiceVM", "Registrando novo serviço: $newService")
            database.barberserviceDao().insert(newService)
            Log.d("CreateBarberServiceVM", "Novo serviço registrado com sucesso!")
            logAllBarberServices()
        }
    }

    /**
     * Log de todos os serviços para debug.
     */
    private fun logAllBarberServices() {
        viewModelScope.launch(Dispatchers.IO) {
            val barberServices = database.barberserviceDao().getAllBarberServices()
            Log.d("CreateBarberServiceVM", "Lista de todos os serviços de barbeiro:")
            barberServices.forEach {
                Log.d(
                    "BarberServiceLog",
                    "Id: ${it.barberServiceId}, BarberId: ${it.barberId}, ServiceId: ${it.serviceId}, Price: ${it.price}, Duration: ${it.duration}, Active: ${it.isActive}"
                )
            }
        }
    }
}
