package com.example.barberapp.ChooseService

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Service
import kotlinx.coroutines.launch

class ChooseServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val serviceDao = AppDatabase(application).serviceDao()

    // LiveData para observar os serviços disponíveis
    val services: LiveData<List<Service>> = serviceDao.getAllServices()

    fun getServiceById(id: Int): LiveData<Service?> {
        val result = MutableLiveData<Service?>()
        viewModelScope.launch {
            result.postValue(serviceDao.getServiceById(id))
        }
        return result
    }

    fun getServicesByIds(ids: List<Int>): LiveData<List<Service>> {
        return serviceDao.getServicesByIds(ids)
    }
}

