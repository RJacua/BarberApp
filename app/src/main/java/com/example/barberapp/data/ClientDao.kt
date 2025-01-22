package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ClientDao: BaseDao<Client> {

    @Query("SELECT * FROM clients")
    fun getAllClients(): List<Client>

    @Query("SELECT * FROM clients WHERE id = :id")
    fun getClientById(id: Int): Client?

}