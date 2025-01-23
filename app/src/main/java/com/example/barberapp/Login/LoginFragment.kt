package com.example.barberapp.Login

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
        // Configurar o View Binding para o fragmento
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar o ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LoginViewModel::class.java]

        // Configuração do botão de login
        binding.loginbtn.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(email, password)
            }
        }

        // Configuração do botão de registro
        binding.signupbtn.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun authenticateUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val (result, user) = withContext(Dispatchers.IO) {
                    viewModel.login(email, password)
                }

                when (result) {
                    "client" -> {
                        Log.d("login", "client")
                        UserSession.loggedInClient = viewModel.loggedInClient.value // Salva o cliente na sessão
                        UserSession.loggedInBarber = null // Certifica-se de que o barbeiro está nulo
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeClientFragment(user!!)
                        )
                    }
                    "barber" -> {
                        Log.d("login", "barber")
                        UserSession.loggedInBarber = viewModel.loggedInBarber.value // Salva o barbeiro na sessão
                        UserSession.loggedInClient = null // Certifica-se de que o cliente está nulo
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeBarberFragment(user!!)
                        )
                    }
                    else -> {
                        Log.d("login", "Email ou senha incorretos")
                        Toast.makeText(context, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("login", "Erro durante o login", e)
                Toast.makeText(context, "Ocorreu um erro. Tente novamente!", Toast.LENGTH_SHORT).show()
            }
        }
    }



}