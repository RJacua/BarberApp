package com.example.barberapp.Register

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application) }

    private var selectedBarbershopId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Configurar o Spinner com as barbearias
        setupSpinner()

        // Tornar o Spinner visível apenas se o Switch estiver ativo
        binding.registerSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.registerBarberShopId.visibility = View.VISIBLE
            } else {
                binding.registerBarberShopId.visibility = View.GONE
            }
        }

        binding.registerbtn.setOnClickListener {
            registerUser()
        }

        binding.backlogin.setOnClickListener {
            findNavController().navigateUp() // Volta para o fragmento anterior
        }

        return binding.root
    }

    private fun setupSpinner() {
        // Inicializar o adaptador com uma lista vazia para evitar problemas visuais
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            mutableListOf<String>() // Lista vazia inicial
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.registerBarberShopId.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            if (!barbershops.isNullOrEmpty()) {
                val barberShopNames = barbershops.map { it.name } // Obter os nomes
                adapter.clear()
                adapter.addAll(barberShopNames) // Adicionar os nomes ao adaptador
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Nenhuma barbearia encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar seleção
        binding.registerBarberShopId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBarbershopId = viewModel.barbershops.value?.get(position)?.barbershopId
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBarbershopId = null
            }
        }
    }


    private fun registerUser() {
        val name = binding.registerName.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val barbershopIdDrop = selectedBarbershopId
        val isBarber = binding.registerSwitch.isChecked

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (isBarber) {
            // Registro de barbeiro com callback
            viewModel.registerBarber(name, email, password, "algo", barbershopIdDrop!!) { isLoggedIn ->
                if (isLoggedIn) {
                    // Sucesso no login e registro, navega para o HomeBarberFragment
                    viewModel.createDefaultServicesForBarber(UserSession.loggedInBarber!!.barberId)
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
