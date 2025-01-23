package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ClientDao: BaseDao<Client> {

    @Query("SELECT * FROM clients")
    fun getAllClients(): List<Client>

    @Query("SELECT * FROM clients WHERE clientId = :id")
    fun getClientById(id: Int): Client?

    @Query("SELECT * FROM clients WHERE email = :email")
    fun getClientByEmail(email: String): Client?
}