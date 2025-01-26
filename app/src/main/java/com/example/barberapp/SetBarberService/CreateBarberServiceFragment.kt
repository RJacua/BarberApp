package com.example.barberapp.SetBarberService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.barberapp.Register.CreateBarberServiceViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentCreateBarberServiceBinding
import java.sql.Time

class CreateBarberServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateBarberServiceBinding
    private val viewModel: CreateBarberServiceViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBarberServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar o argumento serviceId
        val args = CreateBarberServiceFragmentArgs.fromBundle(requireArguments())
        val serviceId = args.serviceId

        // Recuperar o barberId do barbeiro logado
        val barberId = UserSession.loggedInBarber!!.barberId

        if (barberId != null) {
            // Carregar informações do serviço para edição
            viewModel.loadService(barberId, serviceId)
            observeServiceData(serviceId)
        } else {
            Log.e("CreateBarberService", "Nenhum barbeiro logado!")
            findNavController().navigate(
                CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToLoginFragment()
            )
        }

        setupNumberPickers()

        binding.btnCancelBarberServiceCreation.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupNumberPickers() {
        binding.pickerHours.apply {
            minValue = 0
            maxValue = 4
        }

        binding.pickerMinutes.apply {
            minValue = 0
            maxValue = 3
            displayedValues = arrayOf("0", "15", "30", "45")
        }
    }

    private fun observeServiceData(serviceId: Int) {
        viewModel.loadServiceName(serviceId)


        viewModel.serviceName.observe(viewLifecycleOwner) { serviceName ->
            binding.textServiceName.text = serviceName ?: "Unknown Service"
        }


        // Observar os dados do serviço do barbeiro
        viewModel.service.observe(viewLifecycleOwner) { barberService ->
            if (barberService != null) {
                // Preencher os campos com as informações do serviço
                binding.pickerHours.value = barberService.duration.hours
                binding.pickerMinutes.value =
                    barberService.duration.minutes / 15 // Ajusta para os valores do NumberPicker
                binding.priceInput.setText(barberService.price.toString())
                binding.tglActiveService.isChecked = barberService.isActive

                // Configurar botão de salvar
                setupSaveButton(barberService.barberId, barberService.serviceId)
            } else {
                Log.e("CreateBarberService", "Serviço não encontrado!")
                Toast.makeText(
                    requireContext(), "Erro ao carregar informações do serviço.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSaveButton(barberId: Int, serviceId: Int) {
        binding.btnCreateBarberService.setOnClickListener {
            val hours = binding.pickerHours.value
            val minutes = binding.pickerMinutes.displayedValues[binding.pickerMinutes.value].toInt()
            val duration = Time.valueOf("$hours:$minutes:00")
            val price = binding.priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val isActive = binding.tglActiveService.isChecked

            // Atualizar serviço existente
            viewModel.updateBarberService(barberId, serviceId, duration, price, isActive)
            Toast.makeText(
                requireContext(), "Serviço atualizado com sucesso!", Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }
}



