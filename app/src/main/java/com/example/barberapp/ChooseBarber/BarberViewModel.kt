package com.example.barberapp.ChooseBarber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber


class BarberViewModel(application: Application) : AndroidViewModel(application) {

    private val barberDao = AppDatabase(application).barberDao()

    // LiveData para observar os barbeiros disponíveis
    val barbers: List<Barber> = barberDao.getAllBarbers()
}