package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentHomeClientBinding

class HomeClientFragment : Fragment() {

    private lateinit var binding: FragmentHomeClientBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeClientBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBarberShop.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberShopFragment())
        }

        binding.btnBarber.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberFragment())
        }

        binding.btnServices.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToServiceFragment())
        }

        binding.btnappointment.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToAppointmentFragment())
        }

    }

}

