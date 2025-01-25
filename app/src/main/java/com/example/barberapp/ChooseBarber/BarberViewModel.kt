package com.example.barberapp.ChooseBarber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber

class BarberViewModel(application: Application) : AndroidViewModel(application) {

    private val barberDao = AppDatabase(application).barberDao()

    // LiveData para observar os barbeiros disponíveis
    val barbers: LiveData<List<Barber>> = barberDao.getBarbersByBarbershopId(UserSession.selectedBarberShopId!!)

    private val _selectedBarber = MutableLiveData<Barber?>()
    val selectedBarber: LiveData<Barber?> = _selectedBarber

    fun selectBarber(barber: Barber) {
        _selectedBarber.value = barber
    }

    fun getBarberById(id: Int): LiveData<Barber?> {
        return barberDao.getBarberById(id)
    }
}