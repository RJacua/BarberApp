package com.example.barberapp.Service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val serviceDao = AppDatabase(application).serviceDao()

    // LiveData para observar os serviços disponíveis
    val services: LiveData<List<Service>> = serviceDao.getAllServices()
}

