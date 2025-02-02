package com.example.barberapp.GalleryBarber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BarberGalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _photos = MutableLiveData<List<String>>()
    val photos: LiveData<List<String>> get() = _photos

    /**
     * Define as fotos no LiveData
     */
    fun setPhotos(photoPaths: List<String>) {
        _photos.value = photoPaths
    }
}
