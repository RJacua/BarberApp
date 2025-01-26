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

    // Usar activityViewModels para compartilhar o estado entre os fragments
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

        // Acessar informações do barbeiro logado pelo LoginViewModel
        val barberId = UserSession.loggedInBarber?.barberId;

        if (barberId != null) {
            val barber = UserSession.loggedInBarber
            if (barber != null) {
//                binding.barberTitle.text = "Welcome ${barber.name}"
                val welcomeText = getString(R.string.user_title, barber.name)
                binding.barberTitle.text = welcomeText
            } else {
                // Redireciona para o login se não houver barber na sessão
                findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
                return
            }
        } else {
            // Redireciona para o login caso não haja barberId
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
            return
        }

        // Botão para gerenciar serviços
        binding.btnYourServices.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToBarberServiceFragment())
        }

        // Botão para gerenciar agenda
        binding.btnYourSchedule.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToScheduleFragment())
        }


        //Botão para Camera
        binding.btnYourGallery.setOnClickListener{
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToGalleryFragment())
        }

        // Botão para logout
        binding.btnBarberLogout.setOnClickListener {
            loginViewModel.logout() // Limpa o estado global e SharedPreferences
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
        }

        //Botão para meus appoinments
        binding.btnAppointments.setOnClickListener {
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToBarberAppointmentsFragment())
        }
    }
}



