package com.example.barberapp.MyAppointmentsClient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.UserSession
import com.example.barberapp.UtilityClasses.AppointmentDetails
import com.example.barberapp.databinding.FragmentAppointmentsBinding
import com.example.barberapp.databinding.FragmentAppointmentsItemBinding


class ClientAppointmentsFragment : Fragment() {

    private val viewModel by viewModels<AppointmentViewModel>()
    private lateinit var binding: FragmentAppointmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar o RecyclerView
        binding.appointmentsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<AppointmentDetails, AppointmentsViewHolder>(appointmentDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
                val itemBinding = FragmentAppointmentsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AppointmentsViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: AppointmentsViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }
        binding.appointmentsList.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
        }

        // Carregar as marcações
        viewModel.loadAppointments(UserSession.loggedInClient!!.clientId)

        binding.btnback.setOnClickListener{
            findNavController().navigateUp() // Volta para o fragmento anterior
        }
    }


    inner class AppointmentsViewHolder(private val binding: FragmentAppointmentsItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(details: AppointmentDetails) {
                binding.textViewBarberShop.text = details.barbershopName
                binding.textViewBarber.text = details.barberName
                binding.textViewService.text = details.serviceName
                binding.textViewPrice.text = "€${details.price}"
                binding.textViewDate.text = details.date
                binding.textViewTime.text = details.time

                // Verificar o estado e aplicar cor cinza se não for "ativo"
                if (details.status != "Ativo") {
                    binding.root.alpha = 0.5f // Tornar o item semi-transparente
                    binding.root.setBackgroundColor(
                        binding.root.context.getColor(android.R.color.darker_gray) // Cor cinza
                    )
                } else {
                    binding.root.alpha = 1f // Totalmente opaco
                    binding.root.setBackgroundColor(
                        binding.root.context.getColor(android.R.color.transparent) // Sem cor de fundo
                    )
                }
            }


        }

        private val appointmentDiffer = object : DiffUtil.ItemCallback<AppointmentDetails>() {
            override fun areItemsTheSame(oldItem: AppointmentDetails, newItem: AppointmentDetails) =
                oldItem.appointmentId == newItem.appointmentId

            override fun areContentsTheSame(
                oldItem: AppointmentDetails,
                newItem: AppointmentDetails
            ) =
                oldItem == newItem
        }
}
