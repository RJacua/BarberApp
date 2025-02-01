package com.example.barberapp.MyAppointmentsBarber

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.UtilityClasses.AppointmentDetails
import com.example.barberapp.databinding.FragmentBarberAppointmentsBinding
import com.example.barberapp.databinding.FragmentBarberAppointmentsItemBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appointmentsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter =
            object :
                ListAdapter<AppointmentDetails, BarberAppointmentsViewHolder>(appointmentDiffer) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BarberAppointmentsViewHolder {
                    val itemBinding =
                        FragmentBarberAppointmentsItemBinding.inflate( // Usando o layout do cliente
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

        val barberId = UserSession.loggedInBarber?.barberId
        if (barberId != null) {
            viewModel.loadAppointments(barberId)
        } else {
            Toast.makeText(requireContext(), "Barber not found.", Toast.LENGTH_SHORT).show()
        }

        // Button to go back
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    inner class BarberAppointmentsViewHolder(private val binding: FragmentBarberAppointmentsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(details: AppointmentDetails) {

            val price = formatPrice(details.price)

            binding.textViewClientName.text = details.barberName
            binding.textViewService.text = details.serviceName
            binding.textViewPrice.text = "€${price}"
            binding.textViewDate.text = details.date
            binding.textViewTime.text = details.time

            // ✅ Sempre redefinir a aparência corretamente antes de definir os estados
            binding.textViewClientName.setTextColor(Color.WHITE)
            binding.textViewClientName.alpha = 1f
            binding.textViewService.setTextColor(Color.WHITE)
            binding.textViewService.alpha = 1f
            binding.textViewPrice.setTextColor(Color.WHITE)
            binding.textViewPrice.alpha = 1f
            binding.textViewDate.setTextColor(Color.WHITE)
            binding.textViewDate.alpha = 1f
            binding.textViewTime.setTextColor(Color.WHITE)
            binding.textViewTime.alpha = 1f

            //Changing the appearance based on the appointment status
            when (details.status.lowercase()) {
                "active" -> {
                    binding.textViewClientName.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewService.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewPrice.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewDate.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewTime.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.statusText.text = getString(R.string.active)
                    binding.statusText.setTextColor(Color.WHITE)
                }
                "completed", "missed", "canceled" -> {
                    if(details.status == "Completed"){
                        binding.statusText.text = getString(R.string.completed)
                        binding.statusText.setTextColor(Color.parseColor("#F6BE00"))
                    }
                    else if(details.status == "Missed"){
                        binding.statusText.text = getString(R.string.missed)
                        binding.statusText.setTextColor(Color.RED)
                    }
                    else{
                        binding.statusText.text = getString(R.string.canceled)
                        binding.statusText.setTextColor(Color.RED)
                    }
                    binding.textViewClientName.setTypeface(null, android.graphics.Typeface.NORMAL)
                    binding.textViewService.setTypeface(null, android.graphics.Typeface.NORMAL)
                    binding.textViewPrice.setTypeface(null, android.graphics.Typeface.NORMAL)
                    binding.textViewDate.setTypeface(null, android.graphics.Typeface.NORMAL)
                    binding.textViewTime.setTypeface(null, android.graphics.Typeface.NORMAL)

                    binding.textViewClientName.setTextColor(
                        binding.root.context.getColor(android.R.color.white)
                    )
                    binding.textViewClientName.setAlpha(0.5f)
                    binding.textViewService.setTextColor(
                        binding.root.context.getColor(android.R.color.white)
                    )
                    binding.textViewService.setAlpha(0.5f)
                    binding.textViewPrice.setTextColor(
                        binding.root.context.getColor(android.R.color.white)
                    )
                    binding.textViewPrice.setAlpha(0.5f)
                    binding.textViewDate.setTextColor(
                        binding.root.context.getColor(android.R.color.white)
                    )
                    binding.textViewDate.setAlpha(0.5f)
                    binding.textViewTime.setTextColor(
                        binding.root.context.getColor(android.R.color.white)
                    )
                    binding.textViewTime.setAlpha(0.5f)

                }

                else -> {
                    binding.root.setBackgroundColor(
                        binding.root.context.getColor(android.R.color.darker_gray)
                    )
                }
            }

            // Setting the Edit button to be always enabled in logic and appearance
            binding.btnEditStatus.apply {
                isEnabled = true
                alpha = 1f
                setOnClickListener {
                    val action = BarberAppointmentsFragmentDirections
                        .actionBarberAppointmentsFragmentToAppointmentDetailsFragment(
                            appointmentId = details.appointmentId
                        )
                    findNavController().navigate(action)
                }
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

