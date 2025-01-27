package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarberShopDao {

    /**
     * Get all barbershops
     *
     * @return
     */
    @Query("SELECT * FROM barbershops")
    fun getAllBarbershops(): LiveData<List<Barbershop>>


    /**
     * Insert all
     *
     * @param barbershops
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(barbershops: List<Barbershop>)

    /**
     * Get barbershop by id
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM barbershops WHERE barbershopId = :id")
    fun getBarbershopById(id: Int): LiveData<Barbershop?>
}
