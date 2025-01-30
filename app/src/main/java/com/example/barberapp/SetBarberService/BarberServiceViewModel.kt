package com.example.barberapp.SetBarberService

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.UtilityClasses.BarberServiceDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarberServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val barberServiceDao = AppDatabase(application).barberserviceDao()

    private val _services = MutableLiveData<List<BarberServiceDetail>>()
    val services: LiveData<List<BarberServiceDetail>> get() = _services

    /**
     * Load barber services and save the information in the _services LiveData.
     * Function created to make possible to use the id.
     *
     * @param barberId
     */
    fun loadBarberServices(barberId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val serviceList = barberServiceDao.getDetailedServicesByBarber(barberId)
            _services.postValue(serviceList)

        }
    }
}
