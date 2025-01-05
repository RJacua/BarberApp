package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface BarberShopDao {
    @Query("SELECT * FROM barbershops")
    fun getAllBarbershops(): LiveData<List<Barbershop>>
}