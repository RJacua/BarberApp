package com.example.barberapp

import android.app.Application
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barbershop
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader

class BarberApp : Application() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        // Obter o DAO usando o AppDatabase
        val barbershopDao = AppDatabase.invoke(this).barbershopDao()

        // Carregar os dados do JSON
        val barbershopList = loadBarbershopsFromJson()

        GlobalScope.launch(Dispatchers.IO) {
            barbershopDao.insertAll(barbershopList)
        }
    }

    private fun loadBarbershopsFromJson(): List<Barbershop> {
        return try {
            val inputStream = resources.openRawResource(R.raw.barber_shop_list)
            val bufferedReader = inputStream.bufferedReader()
            val json = bufferedReader.use { it.readText() }
            val type = object : TypeToken<List<Barbershop>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Retorna uma lista vazia em caso de erro
        }
    }
}
