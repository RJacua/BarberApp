package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ClientDao: BaseDao<Client> {

    /**
     * Get all clients
     *
     * @return
     */
    @Query("SELECT * FROM clients")
    fun getAllClients(): List<Client>

    /**
     * Get client by id
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM clients WHERE clientId = :id")
    fun getClientById(id: Int): Client?

    /**
     * Get client by email
     *
     * @param email
     * @return
     */
    @Query("SELECT * FROM clients WHERE email = :email")
    fun getClientByEmail(email: String): Client?
}