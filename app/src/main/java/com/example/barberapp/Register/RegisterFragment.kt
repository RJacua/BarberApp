package com.example.barberapp.Register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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

        return binding.root
    }

    private fun registerUser() {
        val name = binding.registerName.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val isBarber = binding.registerSwitch.isChecked

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (isBarber) {
            viewModel.registerBarber(name, email, password)
        } else {
            viewModel.registerClient(name, email, password)
        }

        Toast.makeText(requireContext(), "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
    }

}