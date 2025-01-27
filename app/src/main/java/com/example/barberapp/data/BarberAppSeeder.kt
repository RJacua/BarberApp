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

        val barbershopDao = AppDatabase.invoke(this).barbershopDao()
        val serviceDao = AppDatabase.invoke(this).serviceDao()

        // load data from JSON
        val barbershopList = loadBarbershopsFromJson()
        val serviceList = loadServicesFromJson()

        // insert dadt from JSON
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

    /**
     * Load barbershops from json
     *
     * @return
     */
    private fun loadBarbershopsFromJson(): List<Barbershop> {
        return try {
            val inputStream = resources.openRawResource(R.raw.barber_shop_list)
            val bufferedReader = inputStream.bufferedReader()
            val json = bufferedReader.use { it.readText() }
            val type = object : TypeToken<List<Barbershop>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // returns empty list in case of error
        }
    }

    /**
     * Load services from json
     *
     * @return
     */
    private fun loadServicesFromJson(): List<Service> {
        return try {
            val inputStream = resources.openRawResource(R.raw.service_list)
            val bufferedReader = inputStream.bufferedReader()
            val json = bufferedReader.use { it.readText() }
            val type = object : TypeToken<List<Service>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // returns empty list in case of error
        }
    }
}
