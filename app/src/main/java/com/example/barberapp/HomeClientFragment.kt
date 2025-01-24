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
import com.example.barberapp.databinding.FragmentHomeClientBinding

class HomeClientFragment : Fragment() {

    private lateinit var binding: FragmentHomeClientBinding
    private val viewModel by viewModels<BarbershopViewModel>() // ViewModel para acessar o banco de dados

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
            viewModel.getBarbershopById(selectedId!!).observe(viewLifecycleOwner) { barbershop ->
                if (barbershop != null) {
                    binding.btnBarberShop.text = barbershop.name // Exibir o nome
                } else {
                    binding.btnBarberShop.text = "Selecionar Barbearia"
                }
            }
        } else {
            binding.btnBarberShop.text = "Select Barber Shop"
        }

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

