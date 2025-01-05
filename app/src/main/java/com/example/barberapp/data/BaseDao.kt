package com.example.barberapp.data

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

interface BaseDao<T> {
    @Insert
    suspend fun insert(t: T): Long

    @Update
    suspend fun update(t: T): Int

    @Delete
    suspend fun delete(t: T): Int
}