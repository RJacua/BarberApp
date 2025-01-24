package com.example.barberapp.BarberService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.Register.CreateBarberServiceViewModel
import com.example.barberapp.databinding.FragmentCreateBarberServiceBinding
import java.sql.Time

class CreateBarberServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateBarberServiceBinding
    private val viewModel: CreateBarberServiceViewModel by viewModels { ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application) }

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


            val serviceId = binding.servicesDrop.selectedItem.toString().toInt()
            val duration = "$hours:$minutes:00"
            val price: Double = if (binding.priceInput.toString() == "") {
                Log.d("CreateBarberServiceFragment", "Price Input Vazio")
                0.0 // Valor padrão se o campo estiver vazio
            } else {
                try {
                    Log.d("CreateBarberServiceFragment", "O preço é ${binding.priceInput}")
                    binding.priceInput.text.toString().trim().replace(',', '.').toDouble() // Tente converter para Double
                } catch (e: NumberFormatException) {
                    Log.e("CreateBarberServiceFragment", "Erro ao converter o preço: $binding.priceInput", e)
                    0.0 // Valor padrão se a conversão falhar
                }
            }

            Log.d("CreateBarberServiceFragment", "Criando serviço para barberId: $barberId com duração: $duration")
            viewModel.registerBarberService(barberId = barberId, serviceId = serviceId, duration = Time.valueOf(duration), price = price)
            Toast.makeText(context, "Serviço criado com sucesso!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToBarberServiceFragment())
        }

        binding.btnCancelBarberServiceCreation.setOnClickListener {
            findNavController().navigate(CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToBarberServiceFragment())
        }
    }
}
