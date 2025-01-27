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

    val services: LiveData<List<Service>> = serviceDao.getAllServices()

    /**
     * Get services by ids
     *
     * @param ids
     * @return
     */
    fun getServicesByIds(ids: List<Int>): LiveData<List<Service>> {
        return serviceDao.getServicesByIds(ids)
    }
}

