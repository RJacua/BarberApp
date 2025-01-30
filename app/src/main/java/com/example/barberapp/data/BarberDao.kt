package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface BarberDao: BaseDao<Barber> {

    /**
     * Get all barbers list for login
     *
     * @return
     */
    @Query("SELECT * FROM barbers")
    suspend fun getAllBarbersList(): List<Barber>

    /**
     * Get barber by id for login
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM barbers WHERE barberId = :id")
    suspend fun getBarberByIdLogin(id: Int): Barber?

    /**
     * Get all barbers
     *
     * @return
     */
    @Query("SELECT * FROM barbers")
    fun getAllBarbers(): LiveData<List<Barber>>

    /**
     * Get barber by id
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM barbers WHERE barberId = :id")
    fun getBarberById(id: Int): LiveData<Barber?>

    /**
     * Get barber by email
     *
     * @param email
     * @return
     */
    @Query("SELECT * FROM barbers WHERE email = :email")
    suspend fun getBarberByEmail(email: String): Barber?

    /**
     * Get barbers by barbershop id
     *
     * @param barbershopId
     * @return
     */
    @Query("SELECT * FROM barbers WHERE barbershopId = :barbershopId")
    fun getBarbersByBarbershopId(barbershopId: Int): LiveData<List<Barber>>
}