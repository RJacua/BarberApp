package com.example.barberapp.Register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.registerbtn.setOnClickListener {
            registerUser()
        }

        binding.backlogin.setOnClickListener {
            findNavController().navigateUp() // Volta para o fragmento anterior
        }

        return binding.root
    }

    private fun registerUser() {
        val name = binding.registerName.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val barbershopIdDrop = binding.registerBarberShopId.selectedItem.toString().toInt()
        val isBarber = binding.registerSwitch.isChecked

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (isBarber) {
            // Registro de barbeiro com callback
            viewModel.registerBarber(name, email, password, "algo", barbershopIdDrop) { isLoggedIn ->
                if (isLoggedIn) {
                    // Sucesso no login e registro, navega para o HomeBarberFragment
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeBarberFragment())
                } else {
                    // Falha no login
                    Toast.makeText(requireContext(), "Falha no registro/login do barbeiro.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Registro de cliente com callback
            viewModel.registerClient(name, email, password) { isLoggedIn ->
                if (isLoggedIn) {
                    // Sucesso no login e registro, navega para o HomeClientFragment
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeClientFragment())
                } else {
                    // Falha no login
                    Toast.makeText(requireContext(), "Falha no registro/login do cliente.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
