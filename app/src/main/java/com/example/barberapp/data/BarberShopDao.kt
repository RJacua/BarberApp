package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarberShopDao {

    // Consulta para obter todas as barbearias
    @Query("SELECT * FROM barbershops")
    fun getAllBarbershops(): LiveData<List<Barbershop>>

    // Função para inserir uma lista de barbearias
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(barbershops: List<Barbershop>)
}
