package com.example.barberapp.MyAppointmentsClient

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.MyAppointmentsBarber.AppointmentDetailsFragmentArgs
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.UtilityClasses.AppointmentDetails
import com.example.barberapp.databinding.FragmentAppointmentsBinding
import com.example.barberapp.databinding.FragmentAppointmentsItemBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ClientAppointmentsFragment : Fragment() {

    private val viewModel by viewModels<AppointmentViewModel>()
    private lateinit var binding: FragmentAppointmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->


            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm")

            // Ordenar primeiro os "Active" pela data mais próxima e depois os outros pela data mais próxima
            val sortedAppointments = appointments.sortedWith(

                compareBy<AppointmentDetails> { it.status.lowercase() != "active" }
                    .thenBy { LocalDate.parse(it.date + " " + it.time, dateFormatter)
                    }
            )
            adapter.submitList(sortedAppointments)
        }

        // load appointments
        viewModel.loadAppointments(UserSession.loggedInClient!!.clientId)



    }


    inner class AppointmentsViewHolder(private val binding: FragmentAppointmentsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(details: AppointmentDetails) {
            val price = formatPrice(details.price)
            
            binding.textViewBarberShop.text = details.barbershopName
            binding.textViewBarber.text = details.barberName
            binding.textViewService.text = details.serviceName
            binding.textViewPrice.text = "€${price}"
            binding.textViewDate.text = details.date
            binding.textViewTime.text = details.time

            // ✅ Sempre redefinir a aparência corretamente antes de definir os estados
            binding.textViewBarberShop.setTextColor(Color.WHITE)
            binding.textViewBarberShop.alpha = 1f
            binding.textViewBarber.setTextColor(Color.WHITE)
            binding.textViewBarber.alpha = 1f
            binding.textViewService.setTextColor(Color.WHITE)
            binding.textViewService.alpha = 1f
            binding.textViewPrice.setTextColor(Color.WHITE)
            binding.textViewPrice.alpha = 1f
            binding.textViewDate.setTextColor(Color.WHITE)
            binding.textViewDate.alpha = 1f
            binding.textViewTime.setTextColor(Color.WHITE)
            binding.textViewTime.alpha = 1f

            if (details.status == "Active") {
                binding.btnStatus.text = getString(R.string.cancel)
                binding.btnStatus.isEnabled = true
                binding.btnStatus.alpha = 1f
                binding.statusText.alpha = 0f
            } else {
                binding.statusText.alpha = 1f
                if(details.status == "Canceled") {
                    binding.statusText.text = getString(R.string.canceled)
                    binding.statusText.setTextColor(Color.RED)
                }
                else if(details.status == "Missed"){
                    binding.statusText.text = getString(R.string.missed)
                    binding.statusText.setTextColor(Color.RED)
                }
                else{
                    binding.statusText.text = getString(R.string.completed)
                    binding.statusText.setTextColor(Color.parseColor("#F6BE00"))
                }
                binding.btnStatus.isEnabled = false
                binding.btnStatus.alpha = 0f

                // Aplica estilo de desativado
                binding.textViewBarberShop.alpha = 0.5f
                binding.textViewBarber.alpha = 0.5f
                binding.textViewService.alpha = 0.5f
                binding.textViewPrice.alpha = 0.5f
                binding.textViewDate.alpha = 0.5f
                binding.textViewTime.alpha = 0.5f
            }

            // ✅ Corrigindo o clique no botão para não modificar diretamente o objeto
            binding.btnStatus.setOnClickListener {
                binding.btnStatus.text = getString(R.string.canceled)
                binding.btnStatus.isEnabled = false
                binding.btnStatus.alpha = 0.5f

                // Atualizar status da lista corretamente
                viewModel.cancelAppointment(details.appointmentId)
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

    /**
     * Changes the price display to return 2 decimals
     *
     * @param price
     * @return String
     */
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("0.00") // Garante sempre duas casas decimais
        return formatter.format(price)
    }
}
