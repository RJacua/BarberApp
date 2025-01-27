package com.example.barberapp.Login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isKeepLoggedIn = sharedPreferences.getBoolean("keep_logged_in", false)

        if (!isKeepLoggedIn) {
            sharedPreferences.edit().clear().apply()
            UserSession.clearSession()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkboxKeepLogged.isChecked = UserSession.isKeepLoggedIn // Restaura o estado

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LoginViewModel::class.java]

        Log.d("Login", "O user assim q entra no app: ${UserSession.loggedInBarber?.barberId}")

        // redirect to Home if user is logged
        if(UserSession.loggedInBarber != null || UserSession.loggedInClient != null){
            redirectToHome()
        }

        // login button set up
        binding.loginbtn.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill up all fields.", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, password)
            }
        }

        binding.signupbtn.setOnClickListener() {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.checkboxKeepLogged.setOnCheckedChangeListener { _, isChecked ->
            UserSession.isKeepLoggedIn = isChecked // update user session
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("keep_logged_in", isChecked).apply()
        }


    }

    /**
     * Redirect to home
     *
     */
    private fun redirectToHome() {
        Log.d("LoginFragment", "Entrando em redirectToHome")
        val barber = UserSession.loggedInBarber
        val client = UserSession.loggedInClient

        if (barber != null) {
            Log.d("LoginFragment", "Barbeiro detectado: ${barber.name}")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeBarberFragment())
        } else if (client != null) {
            Log.d("LoginFragment", "Cliente detectado: ${client.name}")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeClientFragment())
        } else {
            Log.d("LoginFragment", "Nenhum usuário logado detectado")
        }
    }

    /**
     * Authenticate user
     *
     * @param email
     * @param password
     */
    private fun authenticateUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val (result, userName) = withContext(Dispatchers.IO) {
                    viewModel.login(email, password)
                }

                when (result) {
                    "client" -> {
                        Log.d("LoginFragment", "Cliente logado: $userName")
                        UserSession.loggedInClient = viewModel.loggedInClient.value
                        redirectToHome()
                    }
                    "barber" -> {
                        Log.d("LoginFragment", "Barbeiro logado: $userName")
                        UserSession.loggedInBarber = viewModel.loggedInBarber.value
                        redirectToHome()
                    }
                    else -> {
                        Log.d("LoginFragment", "Login falhou")
                        Toast.makeText(context, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginFragment", "Erro durante o login", e)
                Toast.makeText(context, "Ocorreu um erro. Tente novamente!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

