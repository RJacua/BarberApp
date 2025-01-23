package com.example.barberapp.BarberService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.databinding.FragmentCreateBarberServiceBinding

class CreateBarberServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateBarberServiceBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBarberServiceBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.loggedInBarber.observe(viewLifecycleOwner) { barber ->
            if (barber != null) {
                Log.d("CreateBarberServiceFragment", "Barbeiro logado com ID: ${barber.barberId}")
                setupNumberPickers()
                setupBarberServiceCreation(barber.barberId)
            } else {
                Log.e("CreateBarberServiceFragment", "Nenhum usuário logado!")
                findNavController().navigate(CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToLoginFragment())
            }
        }
    }

    private fun setupNumberPickers() {
        val numberPickerHours = binding.pickerHours
        numberPickerHours.minValue = 0
        numberPickerHours.maxValue = 4
        numberPickerHours.wrapSelectorWheel = true

        val numberPickerMinutes = binding.pickerMinutes
        val minuteValues = arrayOf("0", "15", "30", "45")
        numberPickerMinutes.minValue = 0
        numberPickerMinutes.maxValue = minuteValues.size - 1
        numberPickerMinutes.displayedValues = minuteValues
        numberPickerMinutes.wrapSelectorWheel = true
    }

    private fun setupBarberServiceCreation(barberId: Int) {
        binding.btnCreateBarberService.setOnClickListener {
            val hours = binding.pickerHours.value
            val minutes = binding.pickerMinutes.displayedValues[binding.pickerMinutes.value].toInt()
            val duration = "$hours:$minutes:00"

            Log.d("CreateBarberServiceFragment", "Criando serviço para barberId: $barberId com duração: $duration")
            // Chamar função para criar o serviço no banco de dados
        }

        binding.btnCancelBarberServiceCreation.setOnClickListener {
            findNavController().navigate(CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToBarberServiceFragment())
        }
    }
}
