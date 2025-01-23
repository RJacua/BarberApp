package com.example.barberapp.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
            val result = withContext(Dispatchers.IO) {
                viewModel.login(email, password)
            }

            when (result) {
                "client" -> {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeClientFragment())
                }
                "barber" -> {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeBarberFragment())
                }
                else -> {
                    Toast.makeText(context, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}