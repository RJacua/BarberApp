package com.example.barberapp.ChooseBarberShop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop

class BarbershopViewModel(application: Application) : AndroidViewModel(application) {

    private val barbershopDao = AppDatabase(application).barbershopDao()

    val barbershops: LiveData<List<Barbershop>> = barbershopDao.getAllBarbershops()

    /**
     * Get barbershop by id
     *
     * @param id
     * @return
     */
    fun getBarbershopById(id: Int): LiveData<Barbershop?> {
        return barbershopDao.getBarbershopById(id)
    }
}
