package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface RatingDao: BaseDao<Rating> {

    @Query("SELECT * FROM ratings WHERE clientId = :clientId")
    fun getRatingsByClient(clientId: Int): LiveData<List<Rating>>

    @Query("SELECT * FROM ratings WHERE photoUrl = :photoUrl")
    fun getRatingsByPhotoUrl(photoUrl: String): LiveData<List<Rating>>

    @Query("SELECT * FROM ratings WHERE photoUrl = :photoUrl")
    fun getRatingsByPhotoUrlSync(photoUrl: String): List<Rating> // 🔹 Método para uso em coroutines

}