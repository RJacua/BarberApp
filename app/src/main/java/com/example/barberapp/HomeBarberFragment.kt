package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.databinding.FragmentHomeBarberBinding
import com.example.barberapp.databinding.FragmentHomeClientBinding



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
                binding.barberTitle.text = "Welcome ${barber.name}"
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

        // Botão para logout
        binding.btnBarberLogout.setOnClickListener {
            loginViewModel.logout() // Limpa o estado global e SharedPreferences
            findNavController().navigate(HomeBarberFragmentDirections.actionHomeBarberFragmentToLoginFragment())
        }
    }
}



