package com.example.barberapp.ChooseAppointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.R
import com.example.barberapp.databinding.FragmentAppointmentBinding

class AppointmentFragment : Fragment() {

    private val viewModel by viewModels<AppointmentViewModel>()

    private lateinit var binding: FragmentAppointmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        binding.backBtn.setOnClickListener{
            findNavController().navigateUp() // Volta para o fragmento anterior
        }
    }

}