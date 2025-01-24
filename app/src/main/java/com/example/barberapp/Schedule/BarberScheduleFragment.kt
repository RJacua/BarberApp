package com.example.barberapp.Schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentBarberScheduleBinding

class BarberScheduleFragment : Fragment() {

    private val viewModel by viewModels<BarberScheduleViewModel>()

    private lateinit var binding: FragmentBarberScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackScheduleBackToHome.setOnClickListener {
            findNavController().navigate(BarberScheduleFragmentDirections.actionScheduleFragmentToHomeBarberFragment())
        }

    }

}
