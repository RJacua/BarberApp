package com.example.barberapp.ChooseBarber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber

class ChooseBarberViewModel(application: Application) : AndroidViewModel(application) {

    private val barberDao = AppDatabase(application).barberDao()

    val barbers: LiveData<List<Barber>> = barberDao.getBarbersByBarbershopId(UserSession.selectedBarberShopId!!)

    /**
     * Get barber by id
     *
     * @param id
     * @return
     */
    fun getBarberById(id: Int): LiveData<Barber?> {
        return barberDao.getBarberById(id)
    }
}