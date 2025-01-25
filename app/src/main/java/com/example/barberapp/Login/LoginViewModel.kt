package com.example.barberapp.Login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.barberapp.UserSession
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Barber
import com.example.barberapp.data.Client

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase(application)

    // Inicializar SharedPreferences com `by lazy` para evitar erros de acesso precoce
    private val sharedPreferences by lazy {
        application.getSharedPreferences("user_prefs", 0)
    }

    // LiveData para armazenar o barbeiro logado
    private val _loggedInBarber = MutableLiveData<Barber?>()
    val loggedInBarber: LiveData<Barber?> get() = _loggedInBarber

    // LiveData para armazenar o cliente logado
    private val _loggedInClient = MutableLiveData<Client?>()
    val loggedInClient: LiveData<Client?> get() = _loggedInClient

    init {
        // Chamar restoreLoggedInUser no init de forma segura
        restoreLoggedInUser()
    }

    /**
     * Verifica as credenciais de login.
     * Retorna:
     * - "client" se o usuário for um cliente.
     * - "barber" se o usuário for um barbeiro.
     * - null se o login falhar.
     */
    suspend fun login(email: String, password: String): Pair<String?, String?> {
        // Limpa a sessão antes de tentar logar

        // Verificar clientes
        val client = database.clientDao().getAllClients()
            .find { it.email == email && it.password == password }
        if (client != null) {
            _loggedInClient.postValue(client) // Armazenar o objeto Client no LiveData
            _loggedInBarber.postValue(null) // Certificar-se de que não há barbeiro logado
            saveLoggedInClient(client) // Salvar no SharedPreferences
            return Pair("client", client.name)
        }

        // Verificar barbeiros
        val barber = database.barberDao().getAllBarbersList()
            .find { it.email == email && it.password == password }
        if (barber != null) {
            _loggedInBarber.postValue(barber) // Armazenar o objeto Barber no LiveData
            _loggedInClient.postValue(null) // Certificar-se de que não há cliente logado
            saveLoggedInBarber(barber) // Salvar no SharedPreferences
            return Pair("barber", barber.name)
        }

        // Falha no login
        return Pair(null, null)
    }


    /**
     * Retorna o ID do barbeiro logado, se houver.
     */
    fun getLoggedInBarberId(): Int? {
        // Primeiro, tentamos pegar o ID do SharedPreferences diretamente
        val barberId = sharedPreferences.getInt("barber_id", -1)

        // Verificamos se o valor recuperado é válido
        if (barberId != -1) {
            return barberId
        }

        // null, caso não tenha um barbeiro logado
        return null
        /*Log.d("_loggedInBarber2", _loggedInBarber.value?.barberId.toString())
        return _loggedInBarber.value?.barberId*/
    }

    /**
     * Retorna o ID do cliente logado, se houver.
     */
    fun getLoggedInClientId(): Int? {
        // Primeiro, tentamos pegar o ID do SharedPreferences diretamente
        val clientId = sharedPreferences.getInt("barber_id", -1)

        // Verificamos se o valor recuperado é válido
        if (clientId != -1) {
            return clientId
        }

        // null, caso não tenha um barbeiro logado
        return null
        /*Log.d("_loggedInBarber2", _loggedInBarber.value?.barberId.toString())
        return _loggedInBarber.value?.barberId*/
    }

    /**
     * Limpa o estado do usuário logado.
     */
    fun logout() {
        sharedPreferences.edit().clear().apply()
        UserSession.clearSession()
    }




    /**
     * Salvar o estado do barbeiro logado no SharedPreferences
     */
    fun saveLoggedInBarber(barber: Barber) {
        Log.d("_loggedInBarber", barber.barberId.toString())
        sharedPreferences.edit()
            .putInt("barber_id", barber.barberId)
            .apply()
    }

    /**
     * Salvar o estado do cliente logado no SharedPreferences
     */
    fun saveLoggedInClient(client: Client) {
        sharedPreferences.edit()
            .putInt("client_id", client.clientId)
            .apply()
    }

    /**
     * Salvar o estado de "manter logado" no SharedPreferences
     */
    fun saveKeepLoggedIn(keepLogged: Boolean) {
        sharedPreferences.edit()
            .putBoolean("keep_logged_in", keepLogged)
            .apply()
    }

    /**
     * Verificar se o usuário optou por "manter logado"
     */
    fun isKeepLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("keep_logged_in", false)
    }


    /**
     * Restaurar o estado do usuário logado a partir do SharedPreferences
     */
    private fun restoreLoggedInUser() {
        // Restaurar barbeiro
        val barberId = sharedPreferences.getInt("barber_id", -1)
        if (barberId != -1) {
            val barber = database.barberDao().getBarberByIdLogin(barberId)
            if (barber != null) {
                _loggedInBarber.postValue(barber)
                UserSession.loggedInBarber = barber // Atualizar o UserSession
            }
        }

        // Restaurar cliente
        val clientId = sharedPreferences.getInt("client_id", -1)
        if (clientId != -1) {
            val client = database.clientDao().getClientById(clientId)
            if (client != null) {
                _loggedInClient.postValue(client)
                UserSession.loggedInClient = client // Atualizar o UserSession
            }
        }
    }
}
