package com.example.barberapp.ChooseService

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.R
import com.example.barberapp.SetBarberService.BarberServiceViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.UtilityClasses.BarberServiceDetail
import com.example.barberapp.databinding.FragmentChooseServiceBinding
import com.example.barberapp.databinding.FragmentServiceItemBinding
import java.text.DecimalFormat

class ChooseServiceFragment : Fragment() {

    private val viewModelBarberService by viewModels<BarberServiceViewModel>()
    private lateinit var binding: FragmentChooseServiceBinding

    // list to store selected sevices
    private val selectedServiceIds = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barberId = UserSession.selectedBarberId
        if (barberId != null) {
            viewModelBarberService.loadBarberServices(barberId)
        } else {
            Log.e("ChooseServiceFragment", "Barber ID is null!")
        }

        binding.serviceList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<BarberServiceDetail, ServiceViewHolder>(serviceDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
                val itemBinding = FragmentServiceItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ServiceViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

        binding.serviceList.adapter = adapter

        viewModelBarberService.services.observe(viewLifecycleOwner) { barberServices ->
            Log.d("ChooseServiceFragment", "Barber services: $barberServices")

            // display first active services
            val displayItems = barberServices.sortedByDescending { it.isActive }

            // clear list of previous selected services
            selectedServiceIds.clear()
            selectedServiceIds.addAll(UserSession.selectedServiceIds) // fill list with values from user session

            adapter.submitList(displayItems)

            binding.serviceList.post { adapter.notifyDataSetChanged() }
        }


        binding.savebtn.setOnClickListener {
            if (selectedServiceIds.isNotEmpty()) {
                // save to UserSession
                UserSession.selectedServiceIds.clear()
                UserSession.selectedServiceIds.addAll(selectedServiceIds)

                //set all next ids as null
                UserSession.selectedAppointmentTime = null
                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "Please select your services.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.backBtn.setOnClickListener{
            findNavController().navigateUp()
        }
    }


    inner class ServiceViewHolder(private val binding: FragmentServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(serviceDetail: BarberServiceDetail) {
            var stringPrice = "00.00"  // default price
            var duration = "00h00min"  // default duration


            val isAvailable = serviceDetail.isActive
            if (isAvailable) {
                var h = ""
                var m = ""
                var n = ""

                // logic to manipulate duration
                if (serviceDetail.duration.toString().split(":")[0].toInt() != 0) {
                    h = serviceDetail.duration.toString().split(":")[0].replace("0", "") + "h"
                }
                if (serviceDetail.duration.toString().split(":")[1].toInt() != 0) {
                    m = serviceDetail.duration.toString().split(":")[1] + "min"
                }
                if (serviceDetail.duration.toString()
                        .split(":")[0].toInt() != 0 && serviceDetail.duration.toString()
                        .split(":")[1].toInt() != 0
                ) {
                    n = "and"
                }
                duration = "$h $n $m"

                // price format
                val decimalFormat = DecimalFormat("#.00") // guarantees two decimal
                stringPrice = decimalFormat.format(serviceDetail.price)
            }

            //update interface with processed values
            binding.serviceNameText.text = serviceDetail.name
            val priceText = getString(R.string.price_text, stringPrice)
            val durationText = getString(R.string.duration_text, duration)

            binding.servicePriceText.text = priceText
            binding.durationText.text = durationText

            // define if service is active or inactive
            binding.root.isEnabled = isAvailable
            binding.root.alpha = if (isAvailable) 1f else 0.5f

            // visual update based on the selected services
            binding.itemContainerService.isSelected =
                selectedServiceIds.contains(serviceDetail.serviceId)

            // select and deselect on click
            binding.root.setOnClickListener {
                if (selectedServiceIds.contains(serviceDetail.serviceId)) {
                    selectedServiceIds.remove(serviceDetail.serviceId)
                } else {
                    selectedServiceIds.add(serviceDetail.serviceId)
                }

                binding.itemContainerService.isSelected =
                    selectedServiceIds.contains(serviceDetail.serviceId)
            }
        }
    }

    private val serviceDiffer = object : DiffUtil.ItemCallback<BarberServiceDetail>() {
        override fun areItemsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem == newItem
    }
}
