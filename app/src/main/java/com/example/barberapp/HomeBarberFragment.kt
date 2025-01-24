package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.barberapp.databinding.FragmentHomeBarberBinding
import com.example.barberapp.databinding.FragmentHomeClientBinding


class HomeBarberFragment : Fragment() {

    private lateinit var binding: FragmentHomeBarberBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBarberBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val args : HomeBarberFragmentArgs by navArgs()
        binding.barberTitle.setText("Welcome " + args.loggedName)

        binding.btnYourServices.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToBarberServiceFragment())
        }

        binding.btnYourSchedule.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToScheduleFragment())
        }

    }
}

