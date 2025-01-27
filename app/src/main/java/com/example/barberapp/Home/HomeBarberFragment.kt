package com.example.barberapp.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentHomeBarberBinding


class HomeBarberFragment : Fragment() {

    private lateinit var binding: FragmentHomeBarberBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBarberBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barberId = UserSession.loggedInBarber?.barberId;

        if (barberId != null) {
            val barber = UserSession.loggedInBarber
            if (barber != null) {
                val welcomeText = getString(R.string.user_title, barber.name)
                binding.barberTitle.text = welcomeText
            } else {
                // redirect to login if no barber is logged
                findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
                return
            }
        } else {
            // redirect to login of no barber id is found
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
            return
        }

        // button to set up services
        binding.btnYourServices.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToBarberServiceFragment())
        }

        // button to set up schedule
        binding.btnYourSchedule.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToScheduleFragment())
        }


        //button to go my gallery (access to camera and photos)
        binding.btnYourGallery.setOnClickListener{
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToGalleryFragment())
        }

        // logou button
        binding.btnBarberLogout.setOnClickListener {
            loginViewModel.logout() //clean user session and SharedPreferences
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
        }

        //button to my appointments
        binding.btnAppointments.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToBarberAppointmentsFragment())
        }
    }
}



