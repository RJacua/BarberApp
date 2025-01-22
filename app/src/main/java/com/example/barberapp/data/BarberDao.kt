package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BarberDao: BaseDao<Barber> {

    @Query("SELECT * FROM barbers")
    fun getAllBarbers(): List<Barber>

    @Query("SELECT * FROM barbers WHERE id = :id")
    fun getBarberById(id: Int): Barber?
}