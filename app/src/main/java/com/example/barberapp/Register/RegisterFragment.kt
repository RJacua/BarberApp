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

        // set up dropdown with barbershops
        setupSpinner()

        // make dropdown visible only if switch is active
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
            findNavController().navigateUp()
        }

        return binding.root
    }

    /**
     * Setup spinner
     *
     */
    private fun setupSpinner() {

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            mutableListOf<String>() // initial list is empty
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.registerBarberShopId.adapter = adapter

        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            if (!barbershops.isNullOrEmpty()) {
                val barberShopNames = barbershops.map { it.name } // get names of the barbershops
                adapter.clear()
                adapter.addAll(barberShopNames) // add names to adpter
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No barbershops found", Toast.LENGTH_SHORT).show()
            }
        }

        // set up selection
        binding.registerBarberShopId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBarbershopId = viewModel.barbershops.value?.get(position)?.barbershopId
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBarbershopId = null
            }
        }
    }


    /**
     * Register user
     *
     */
    private fun registerUser() {
        val name = binding.registerName.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val barbershopIdDrop = selectedBarbershopId
        val isBarber = binding.registerSwitch.isChecked

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (isBarber) {
            // barber sign up
            viewModel.registerBarber(name, email, password, "I'm a barber", barbershopIdDrop!!) { isLoggedIn ->
                if (isLoggedIn) {
                    // success on login  and sign up, navegate to HomeBarberFragment
                    viewModel.createDefaultServicesForBarber(UserSession.loggedInBarber!!.barberId)
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeBarberFragment())
                } else {
                    // login failed
                    Toast.makeText(requireContext(), "Barber Login/Sign up in failed.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // client sign up
            viewModel.registerClient(name, email, password) { isLoggedIn ->
                if (isLoggedIn) {
                    // success on login  and sign up, navegate to HomeClientFragment
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeClientFragment())
                } else {
                    // login failed
                    Toast.makeText(requireContext(), "Client Login/Sign up in failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
