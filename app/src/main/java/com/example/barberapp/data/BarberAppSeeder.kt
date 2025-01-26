package com.example.barberapp.data

import android.app.Application
import android.util.Log
import com.example.barberapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarberAppSeeder : Application() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        // Obter o DAO usando o AppDatabase
        val barbershopDao = AppDatabase.invoke(this).barbershopDao()

        // Carregar os dados do JSON
        val barbershopList = loadBarbershopsFromJson()

        val serviceDao = AppDatabase.invoke(this).serviceDao()

        // Carregar os dados do JSON
        val serviceList = loadServicesFromJson()


        GlobalScope.launch(Dispatchers.IO) {
            barbershopDao.insertAll(barbershopList)
            serviceDao.insertAll(serviceList)

            withContext(Dispatchers.Main) {
                serviceDao.getAllServices().observeForever { services ->
                    services.forEach { service ->
                        Log.d("BarberApp", "Service: ${service.name}, Description: ${service.description}")
                    }
                }
            }
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

    private fun loadServicesFromJson(): List<Service> {
        return try {
            val inputStream = resources.openRawResource(R.raw.service_list)
            val bufferedReader = inputStream.bufferedReader()
            val json = bufferedReader.use { it.readText() }
            val type = object : TypeToken<List<Service>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Retorna uma lista vazia em caso de erro
        }
    }
}
