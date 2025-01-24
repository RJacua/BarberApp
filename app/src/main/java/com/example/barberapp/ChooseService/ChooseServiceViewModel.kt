package com.example.barberapp.ChooseService

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Service

class ChooseServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val serviceDao = AppDatabase(application).serviceDao()

    // LiveData para observar os serviços disponíveis
    val services: LiveData<List<Service>> = serviceDao.getAllServices()
}

