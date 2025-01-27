package com.example.barberapp.GalleryBarber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.barberapp.UserSession
import java.io.File

class CameraViewModel(private val application: Application) : AndroidViewModel(application) {
    var photoFile: File? = null

    /**
     * Save photo
     *
     */
    fun savephoto(){
        val barberId = UserSession.loggedInBarber!!.barberId
        val barbershopId = UserSession.loggedInBarber!!.barbershopId
        val renameTo = photoFile?.renameTo(File(application.filesDir, "${barbershopId}_${barberId}_${System.currentTimeMillis()}.jpg"))
    }

}
