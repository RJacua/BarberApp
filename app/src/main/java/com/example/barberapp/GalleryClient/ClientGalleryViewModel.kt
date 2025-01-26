package com.example.barberapp.GalleryClient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop

class ClientGalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    // Obter todas as barbearias como LiveData
    val barbershops: LiveData<List<Barbershop>> = database.barbershopDao().getAllBarbershops()

    private val _photos = MutableLiveData<List<Pair<String, String>>>()
    val photos: LiveData<List<Pair<String, String>>> get() = _photos

    fun setPhotos(photoList: List<Pair<String, String>>) {
        _photos.value = photoList
    }

    fun getBarberNameById(barberId: Int): String {
        val barber =  database.barberDao().getBarberByIdLogin(barberId) // Assume que você tem um DAO para acessar o banco
        return barber?.name ?: "Uncknow" // Se não encontrar, retorna "Desconhecido"
    }

}