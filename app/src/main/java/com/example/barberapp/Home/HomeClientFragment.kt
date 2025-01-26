package com.example.barberapp.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.barberapp.ChooseBarberShop.BarbershopViewModel
import com.example.barberapp.ChooseBarber.ChooseBarberViewModel
import com.example.barberapp.ChooseService.ChooseServiceViewModel
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentHomeClientBinding
import kotlinx.coroutines.launch

class HomeClientFragment : Fragment() {

    private lateinit var binding: FragmentHomeClientBinding
    private val viewModelBarberShop by viewModels<BarbershopViewModel>() // ViewModel para acessar o banco de dados BarberShor
    private val viewModelBarber by viewModels<ChooseBarberViewModel>() // ViewModel para acessar o banco de dados Barber
    private val viewModelChooseService by viewModels<ChooseServiceViewModel>()
    private val viewModelHomeClient by viewModels<HomeClientViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeClientBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val clientName = UserSession.loggedInClient!!.name
        val welcomeText = getString(R.string.user_title, clientName)
        binding.clientTitle.text = welcomeText

        // Verificar se há uma barbearia selecionada
        if (UserSession.selectedBarberShopId != null) {
            val selectedId = UserSession.selectedBarberShopId
            // Consultar o nome da barbearia com base no ID
            viewModelBarberShop.getBarbershopById(selectedId!!)
                .observe(viewLifecycleOwner) { barbershop ->
                    if (barbershop != null) {
                        binding.btnBarberShop.text = barbershop.name // Exibir o nome
                    } else {
                        binding.btnBarberShop.text = "Select Barber Shop"
                    }
                }
        } else {
            binding.btnBarberShop.text = "Select Barber Shop"
        }

        // Verificar se há um barbeiro selecionado
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

        // Verificar se há serviços selecionados
        if (UserSession.selectedServiceIds.isNotEmpty()) {
            viewModelChooseService.getServicesByIds(UserSession.selectedServiceIds)
                .observe(viewLifecycleOwner) { services ->
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

        // Verificar se há um horário selecionado
        if (UserSession.selectedAppointmentTime != null) {
            val selectedTime = UserSession.selectedAppointmentTime
            val selectedDate = UserSession.selectedAppointmentDate
            // Exibir o horário selecionado no botão
            binding.btnAppointment.text = selectedDate + " - " +
                selectedTime // Alterar o texto do botão para o horário selecionado
        } else {
            // Caso não haja horário selecionado, exibir o texto padrão
            binding.btnAppointment.text = "Select Appointment"
        }

        binding.btnBarberShop.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberShopFragment())
        }

        if (UserSession.selectedBarberShopId == null) {
            binding.btnBarber.apply {
                alpha = 0.3f
                isEnabled = false
            }
        } else {
            binding.btnBarber.apply {
                alpha = 1f
                isEnabled = true
            }
        }

        binding.btnBarber.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToBarberFragment())
        }

        if (UserSession.selectedBarberId == null) {
            binding.btnServices.apply {
                alpha = 0.3f
                isEnabled = false
            }
        } else {
            binding.btnServices.apply {
                alpha = 1f
                isEnabled = true
            }
        }
        binding.btnServices.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToServiceFragment())
        }

        if (UserSession.selectedServiceIds.isEmpty()) {
            binding.btnAppointment.apply {
                alpha = 0.3f
                isEnabled = false
            }
        } else {
            binding.btnAppointment.apply {
                alpha = 1f
                isEnabled = true
            }
        }
        binding.btnAppointment.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToAppointmentFragment())
        }

        if (UserSession.selectedAppointmentTime == null) {
            binding.btnsave.apply {
                alpha = 0.3f
                isEnabled = false
            }
        } else {
            binding.btnsave.apply {
                alpha = 1f
                isEnabled = true
            }
        }
        binding.btnsave.setOnClickListener {
            lifecycleScope.launch {
                val result = viewModelHomeClient.createAppointments()
                result.onSuccess {
                    Toast.makeText(context, "Marcações criadas com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToAppointmentsFragment())
                }.onFailure { exception ->
                    Toast.makeText(context, "Erro: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnClientLogout.setOnClickListener {
            UserSession.clearSession()
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToLoginFragment())

        }

        binding.btnAppointments.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToAppointmentsFragment())
        }

        binding.btnGallery.setOnClickListener {
            findNavController().navigate(HomeClientFragmentDirections.actionHomeClientFragmentToClientGalleryFragment())
        }

    }

}



