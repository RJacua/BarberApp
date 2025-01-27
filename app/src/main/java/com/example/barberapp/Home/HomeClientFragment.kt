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
    private val viewModelBarberShop by viewModels<BarbershopViewModel>()
    private val viewModelBarber by viewModels<ChooseBarberViewModel>()
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

        // verify if a barbershop is selected
        if (UserSession.selectedBarberShopId != null) {
            val selectedId = UserSession.selectedBarberShopId
            // check barbershop name based on id
            viewModelBarberShop.getBarbershopById(selectedId!!)
                .observe(viewLifecycleOwner) { barbershop ->
                    if (barbershop != null) {
                        binding.btnBarberShop.text = barbershop.name // change button name to selected barbershop
                    } else {
                        binding.btnBarberShop.text = getString(R.string.barbershop)
                    }
                }
        } else {
            binding.btnBarberShop.text = getString(R.string.barbershop)
        }

        // verify if a barber is selected
        if (UserSession.selectedBarberId != null) {
            val selectedId = UserSession.selectedBarberId
            // check barber name based on id
            viewModelBarber.getBarberById(selectedId!!).observe(viewLifecycleOwner) { barber ->
                if (barber != null) {
                    binding.btnBarber.text = barber.name // change button name to selected barber
                } else {
                    binding.btnBarber.text = getString(R.string.barber)
                }
            }
        } else {
            binding.btnBarber.text = getString(R.string.barber)
        }

        // verify if any services are selected
        if (UserSession.selectedServiceIds.isNotEmpty()) {
            // check services name based on ids
            viewModelChooseService.getServicesByIds(UserSession.selectedServiceIds)
                .observe(viewLifecycleOwner) { services ->
                    if (services.isNotEmpty()) {
                        val serviceNames = services.joinToString(", ") { it.name }
                        binding.btnServices.text = serviceNames // change button name to selected services
                    } else {
                        binding.btnServices.text = getString(R.string.services)
                    }
                }
        } else {
            binding.btnServices.text = getString(R.string.services)
        }

        //verify if an date and time are selected
        if (UserSession.selectedAppointmentTime != null) {
            val selectedTime = UserSession.selectedAppointmentTime
            val selectedDate = UserSession.selectedAppointmentDate

            binding.btnAppointment.text = selectedDate + " - " +
                selectedTime // change button name to selected date and time
        } else {
            binding.btnAppointment.text = getString(R.string.setAppointments)
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



