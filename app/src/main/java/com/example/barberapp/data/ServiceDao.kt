package com.example.barberapp.data


import androidx.room.Dao
import androidx.room.Query

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    fun getAllServices(): List<Service>

    @Query("SELECT * FROM services WHERE id = :id")
    fun getServiceById(id: Int): Service?
}