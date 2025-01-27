package com.example.barberapp.MyAppointmentsClient

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
            adapter.submitList(appointments)
        }

        // load appointments
        viewModel.loadAppointments(UserSession.loggedInClient!!.clientId)

        binding.btnback.setOnClickListener{
            findNavController().navigateUp()
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

                if (details.status == "Active"){
                    binding.btnStatus.text = getString(R.string.cancel)
                    binding.btnStatus.isEnabled = true
                }
                else {
                    binding.btnStatus.text = getString(R.string.canceled)
                    binding.btnStatus.alpha = 0.5f
                    binding.btnStatus.isEnabled = false
                }

                binding.btnStatus.setOnClickListener {
                    // update status locally
                    details.status = "Canceled"
                    binding.btnStatus.text = getString(R.string.canceled)
                    binding.btnStatus.isEnabled = false

                    // notify ViewModel to update the data base
                    viewModel.cancelAppointment(details.appointmentId)

                    // update the specific item on the list
                    bindingAdapter?.notifyItemChanged(bindingAdapterPosition)
                }

                // verify the state of the appointment and adjust design accordingly
                if (details.status == "Active") {
                    binding.textViewBarberShop.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewBarber.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewService.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewPrice.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewDate.setTypeface(null, android.graphics.Typeface.BOLD)
                    binding.textViewTime.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    binding.textViewBarberShop.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
                    )
                    binding.textViewBarber.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
                    )
                    binding.textViewService.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
                    )
                    binding.textViewPrice.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
                    )
                    binding.textViewDate.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
                    )
                    binding.textViewTime.setTextColor(
                        binding.root.context.getColor(android.R.color.black)
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
