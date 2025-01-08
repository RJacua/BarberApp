package com.example.barberapp.BarberShop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop
import com.example.barberapp.data.BarberShopDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarbershopViewModel(application: Application) : AndroidViewModel(application) {

    private val barbershopDao = AppDatabase(application).barbershopDao()

    // LiveData para observar os barbershops armazenados
    val barbershops: LiveData<List<Barbershop>> = barbershopDao.getAllBarbershops()

    // Função para popular o banco de dados com dados da lista carregada do JSON
    fun populateDatabase(barbershopList: List<Barbershop>) {
        viewModelScope.launch(Dispatchers.IO) {
            barbershopDao.insertAll(barbershopList)
        }
    }
}
