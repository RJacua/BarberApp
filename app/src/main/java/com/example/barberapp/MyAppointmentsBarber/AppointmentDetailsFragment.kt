package com.example.barberapp.MyAppointmentsBarber

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.barberapp.databinding.FragmentAppointmentDetailsBinding

class AppointmentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentDetailsBinding
    private val viewModel: AppointmentDetailsViewModel by viewModels()
    private val args: AppointmentDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appointmentId = args.appointmentId

        viewModel.loadAppointment(appointmentId)
        observeAppointmentData()


        setupStatusDropdown()

        //Button to save
        binding.btnSave.setOnClickListener {
            val selectedStatus = binding.dropdownStatus.selectedItem.toString()
            viewModel.updateAppointmentStatus(appointmentId, selectedStatus)
            Toast.makeText(requireContext(), "Status atualizado com sucesso!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigateUp()
        }

        //Button to go back to Appointment List
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    /**
     * Creates the Status Dropdown for the Appointment Details Fragment
     *
     */
    private fun setupStatusDropdown() {
        val statusOptions = listOf("Active", "Completed", "Missed", "Canceled")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dropdownStatus.adapter = adapter
    }

    private fun observeAppointmentData() {
        viewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            if (appointment != null) {
                binding.textClientName.text = appointment.clientName
                binding.textServiceName.text = appointment.serviceName
                binding.textAppointmentDate.text = appointment.date
                binding.textAppointmentTime.text = appointment.time
                binding.dropdownStatus.setSelection(getStatusIndex(appointment.status))
            } else {
                Log.e("AppointmentDetails", "Erro ao carregar o appointment!")
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar detalhes da marcação.",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }


    /**
     * Basically a "dictionary" that converts the dropdown index to a status
     *
     * @param status
     * @return
     */
    private fun getStatusIndex(status: String): Int {
        return when (status) {
            "Active" -> 0
            "Completed" -> 1
            "Missed" -> 2
            "Canceled" -> 3
            else -> 0
        }
    }
}
