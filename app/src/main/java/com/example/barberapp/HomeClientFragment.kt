package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.barberapp.BarberShop.BarbershopViewModel
import com.example.barberapp.ChooseBarber.BarberViewModel
import com.example.barberapp.ChooseService.ChooseServiceViewModel
import com.example.barberapp.databinding.FragmentHomeClientBinding

class HomeClientFragment : Fragment() {

    private lateinit var binding: FragmentHomeClientBinding
    private val viewModelBarberShop by viewModels<BarbershopViewModel>() // ViewModel para acessar o banco de dados BarberShor
    private val viewModelBarber by viewModels<BarberViewModel>() // ViewModel para acessar o banco de dados Barber
    private val viewModelChooseService by viewModels<ChooseServiceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeClientBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val args : HomeClientFragmentArgs by navArgs()
        binding.clientTitle.setText("Welcome " + args.loggedName)

        // Verificar se há uma barbearia selecionada
        if (UserSession.selectedBarberShopId != null) {
            val selectedId = UserSession.selectedBarberShopId
            // Consultar o nome da barbearia com base no ID
            viewModelBarberShop.getBarbershopById(selectedId!!).observe(viewLifecycleOwner) { barbershop ->
                if (barbershop != null) {
                    binding.btnBarberShop.text = barbershop.name // Exibir o nome
                } else {
                    binding.btnBarberShop.text = "Select Barber Shop"
                }
            }
        } else {
            binding.btnBarberShop.text = "Select Barber Shop"
        }

        // Verificar se há um barbeiroselecionada
        if (UserSession.selectedBarberId != null) {
            val selectedId = UserSession.selectedBarberId
            // Consultar o nome do barbeiro com base no ID
            viewModelBarber.getBarberById(selectedId!!).observe(viewLifecycleOwner) { barber ->
                if (barber != null) {
                    binding.btnBarber.text = barber.name // Exibir o nome
                } else {
                    binding.btnBarber.text = "Select Barber"
                }
            }
        } else {
            binding.btnBarber.text = "Select Barber"
        }

        if (UserSession.selectedServiceIds.isNotEmpty()) {
            viewModelChooseService.getServicesByIds(UserSession.selectedServiceIds).observe(viewLifecycleOwner) { services ->
                if (services.isNotEmpty()) {
                    val serviceNames = services.joinToString(", ") { it.name }
                    binding.btnServices.text = serviceNames
                } else {
                    binding.btnServices.text = "Select Services"
                }
            }
        } else {
            binding.btnServices.text = "Select Services"
        }


        binding.btnBarberShop.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberShopFragment())
        }

        if (UserSession.selectedBarberShopId == null ) {
            binding.btnBarber.isEnabled = false
        }
        else{
            binding.btnBarber.isEnabled = true
        }

        binding.btnBarber.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberFragment())
        }

        if (UserSession.selectedBarberId == null ) {
            binding.btnServices.isEnabled = false
        }
        else{
            binding.btnServices.isEnabled = true
        }
        binding.btnServices.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToServiceFragment())
        }

        if (UserSession.selectedServiceIds.isEmpty() ) {
            binding.btnappointment.isEnabled = false
        }
        else{
            binding.btnappointment.isEnabled = true
        }
        binding.btnappointment.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToAppointmentFragment())
        }

    }

}



