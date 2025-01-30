package com.example.barberapp.Login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase(application)

    private val sharedPreferences by lazy {
        application.getSharedPreferences("user_prefs", 0)
    }

    private val _loggedInBarber = MutableLiveData<Barber?>()
    val loggedInBarber: LiveData<Barber?> get() = _loggedInBarber

    private val _loggedInClient = MutableLiveData<Client?>()
    val loggedInClient: LiveData<Client?> get() = _loggedInClient

    init {
        viewModelScope.launch {
            restoreLoggedInUser()
        }
    }

    /**
     * Login
     *
     * @param email
     * @param password
     * @return
     */
    suspend fun login(email: String, password: String): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            // verify client
            val client = database.clientDao().getAllClients()
                .find { it.email == email && it.password == password }
            if (client != null) {
                _loggedInClient.postValue(client) //  store client obj on LiveData
                _loggedInBarber.postValue(null) // make sure no barber is logged
                saveLoggedInClient(client) //save to SharedPreferences
                return@withContext Pair("client", client.name)
            }

            // verify barber
            val barber = database.barberDao().getAllBarbersList()
                .find { it.email == email && it.password == password }
            if (barber != null) {
                _loggedInBarber.postValue(barber) // store obj barber on LiveData
                _loggedInClient.postValue(null) // make sure no client is logged
                saveLoggedInBarber(barber) // save to SharedPreferences
                return@withContext Pair("barber", barber.name)
            }

            // login failed
            return@withContext Pair(null, null)
        }
    }

    /**
     * Logout
     *
     */
    fun logout() {
        sharedPreferences.edit().clear().apply()
        UserSession.clearSession()
        _loggedInBarber.postValue(null)
        _loggedInClient.postValue(null)
    }


    /**
     * Save logged in barber
     *
     * @param barber
     */
    fun saveLoggedInBarber(barber: Barber) {
        Log.d("_loggedInBarber", barber.barberId.toString())
        sharedPreferences.edit()
            .putInt("barber_id", barber.barberId)
            .apply()
    }

    /**
     * Save logged in client
     *
     * @param client
     */
    fun saveLoggedInClient(client: Client) {
        sharedPreferences.edit()
            .putInt("client_id", client.clientId)
            .apply()
    }


    /**
     * Restore logged in user
     *
     */
    private suspend fun restoreLoggedInUser() {
        withContext(Dispatchers.IO) {
            // restore barber
            val barberId = sharedPreferences.getInt("barber_id", -1)
            if (barberId != -1) {
                val barber = database.barberDao().getBarberByIdLogin(barberId)
                if (barber != null) {
                    _loggedInBarber.postValue(barber)
                    UserSession.loggedInBarber = barber // update UserSession
                }
            }

            // restore client
            val clientId = sharedPreferences.getInt("client_id", -1)
            if (clientId != -1) {
                val client = database.clientDao().getClientById(clientId)
                if (client != null) {
                    _loggedInClient.postValue(client)
                    UserSession.loggedInClient = client // update UserSession
                }
            }
        }
    }
}
