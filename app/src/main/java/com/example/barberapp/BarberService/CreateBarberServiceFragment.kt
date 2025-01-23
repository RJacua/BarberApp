package com.example.barberapp.BarberService

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentCreateBarberServiceBinding


class CreateBarberServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateBarberServiceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateBarberServiceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnCreateBarberService.setOnClickListener {

        }

        binding.btnCancelBarberServiceCreation.setOnClickListener {
            findNavController().navigate(CreateBarberServiceFragmentDirections.actionCreateBarberServiceFragmentToBarberServiceFragment())
        }

        binding.pickerHours.value.toString()
        val numberPickerHours = binding.pickerHours
        numberPickerHours.setMinValue(0)
        numberPickerHours.setMaxValue(4)
        numberPickerHours.wrapSelectorWheel = true

        val numberPickerMinutes = binding.pickerMinutes
        val minuteValues = arrayOf("0", "15", "30", "45")
        numberPickerMinutes.minValue = 0
        numberPickerMinutes.maxValue = minuteValues.size - 1
        numberPickerMinutes.displayedValues = minuteValues
        numberPickerMinutes.wrapSelectorWheel = true

    }
}
