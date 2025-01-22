package com.example.barberapp.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    fun getAllServices(): LiveData<List<Service>>

    @Query("SELECT * FROM services WHERE id = :id")
    fun getServiceById(id: Int): Service?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(services: List<Service>)

}

