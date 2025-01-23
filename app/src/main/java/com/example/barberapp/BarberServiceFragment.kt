package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.barberapp.databinding.FragmentBarberServiceBinding
import com.example.barberapp.databinding.FragmentHomeBarberBinding
import com.example.barberapp.databinding.FragmentHomeClientBinding


class BarberServiceFragment : Fragment() {

    private lateinit var binding: FragmentBarberServiceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBarberServiceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnCreateNewBarberService.setOnClickListener {
            findNavController().navigate(BarberServiceFragmentDirections.actionBarberServiceFragmentToCreateBarberServiceFragment())
        }
    }
}
