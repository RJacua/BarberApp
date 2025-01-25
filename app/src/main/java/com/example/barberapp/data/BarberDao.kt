package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface BarberDao: BaseDao<Barber> {

    @Query("SELECT * FROM barbers")
    suspend fun getAllBarbersList(): List<Barber>

    @Query("SELECT * FROM barbers WHERE barberId = :id")
    fun getBarberByIdLogin(id: Int): Barber?

    @Query("SELECT * FROM barbers")
    fun getAllBarbers(): LiveData<List<Barber>>

    @Query("SELECT * FROM barbers WHERE barberId = :id")
    fun getBarberById(id: Int): LiveData<Barber?>

    @Query("SELECT * FROM barbers WHERE email = :email")
    fun getBarberByEmail(email: String): Barber?

    @Query("SELECT * FROM barbers WHERE barbershopId = :barbershopId")
    fun getBarbersByBarbershopId(barbershopId: Int): LiveData<List<Barber>>
}