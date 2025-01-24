package com.example.barberapp.BarberShop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _selectedBarbershop = MutableLiveData<Barbershop?>()
    val selectedBarbershop: LiveData<Barbershop?> = _selectedBarbershop

    fun selectBarbershop(barbershop: Barbershop) {
        _selectedBarbershop.value = barbershop
    }

    // Função para popular o banco de dados com dados da lista carregada do JSON
    fun populateDatabase(barbershopList: List<Barbershop>) {
        viewModelScope.launch(Dispatchers.IO) {
            barbershopDao.insertAll(barbershopList)
        }
    }

    fun getBarbershopById(id: Int): LiveData<Barbershop?> {
        return barbershopDao.getBarbershopById(id)
    }
}
