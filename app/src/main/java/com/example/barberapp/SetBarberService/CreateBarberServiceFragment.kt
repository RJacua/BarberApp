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

        val args = CreateBarberServiceFragmentArgs.fromBundle(requireArguments())
        val serviceId = args.serviceId

        val barberId = UserSession.loggedInBarber!!.barberId

        if (barberId != null) {
            // Load the service information in the view to be edited
            viewModel.loadService(barberId, serviceId)
            viewModel.loadServiceName(serviceId)

            viewModel.serviceName.observe(viewLifecycleOwner) { serviceName ->
                binding.textServiceName.text = serviceName ?: "Unknown Service"
            }

            viewModel.service.observe(viewLifecycleOwner) { barberService ->
                if (barberService != null) {

                    binding.pickerHours.value = barberService.duration.hours
                    binding.pickerMinutes.value =
                        barberService.duration.minutes / 15 // Ajusta para os valores do NumberPicker
                    binding.priceInput.setText(barberService.price.toString())
                    binding.tglActiveService.isChecked = barberService.isActive

                    setupSaveButton(barberService.barberId, barberService.serviceId)
                } else {
                    Log.e("CreateBarberService", "Service not found!")
                    Toast.makeText(
                        requireContext(), "Error loading the service's info.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Log.e("CreateBarberService", "No user found!")
            findNavController().navigate(
                CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToLoginFragment()
            )
        }

        setupNumberPickers()

        binding.btnCancelBarberServiceCreation.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Setup and starts the number picker
     *
     */
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

    /**
     * Setup the save button's logic
     *
     * @param barberId
     * @param serviceId
     */
    private fun setupSaveButton(barberId: Int, serviceId: Int) {
        binding.btnCreateBarberService.setOnClickListener {
            val hours = binding.pickerHours.value
            val minutes = binding.pickerMinutes.displayedValues[binding.pickerMinutes.value].toInt()
            val duration = Time.valueOf("$hours:$minutes:00")
            val price = binding.priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val isActive = binding.tglActiveService.isChecked

            viewModel.updateBarberService(barberId, serviceId, duration, price, isActive)
            Toast.makeText(
                requireContext(), "Service updated!", Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }
}



