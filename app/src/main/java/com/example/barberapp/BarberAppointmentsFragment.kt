package com.example.barberapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.data.AppointmentDetails
import com.example.barberapp.databinding.FragmentBarberAppointmentsBinding
import com.example.barberapp.databinding.FragmentBarberAppointmentsItemBinding


class BarberAppointmentsFragment : Fragment() {

    private val viewModel by viewModels<BarberAppointmentsViewModel>()
    private lateinit var binding: FragmentBarberAppointmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar o RecyclerView
        binding.appointmentsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<AppointmentDetails, BarberAppointmentsViewHolder>(appointmentDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberAppointmentsViewHolder {
                val itemBinding = FragmentBarberAppointmentsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BarberAppointmentsViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: BarberAppointmentsViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }
        binding.appointmentsList.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
        }

        // Carregar as marcações para o barbeiro logado
        val barberId = UserSession.loggedInBarber?.barberId
        if (barberId != null) {
            viewModel.loadAppointments(barberId)
        } else {
            Toast.makeText(requireContext(), "Barbeiro não encontrado.", Toast.LENGTH_SHORT).show()
        }

        // Configuração do botão de voltar
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp() // Volta para o fragmento anterior
        }
    }

    inner class BarberAppointmentsViewHolder(private val binding: FragmentBarberAppointmentsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(details: AppointmentDetails) {
            binding.ClientName.text = details.clientName
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
