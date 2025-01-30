package com.example.barberapp.GalleryClient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientGalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase(getApplication())

    val barbershops: LiveData<List<Barbershop>> = database.barbershopDao().getAllBarbershops()

    private val _photos = MutableLiveData<List<Pair<String, String>>>()
    val photos: LiveData<List<Pair<String, String>>> get() = _photos

    /**
     * Set photos
     *
     * @param photoList
     */
    fun setPhotos(photoList: List<Pair<String, String>>) {
        _photos.value = photoList
    }

    /**
     * Get barber name by id
     *
     * @param barberId
     * @return
     */
    suspend fun getBarberNameById(barberId: Int): String {
        return withContext(Dispatchers.IO) {
            val barber = database.barberDao().getBarberByIdLogin(barberId)
            barber?.name ?: "Unknown" // Corrigindo "Unknow" para "Unknown"
        }
    }

}