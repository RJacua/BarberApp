package com.example.barberapp.Maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    val barbershops: LiveData<List<Barbershop>> = database.barbershopDao().getAllBarbershops()

}