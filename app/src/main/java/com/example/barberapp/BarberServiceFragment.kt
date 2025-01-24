package com.example.barberapp

import BarberServiceViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.Service.ServiceFragment.ServiceViewHolder
import com.example.barberapp.Service.ServiceViewModel
import com.example.barberapp.data.BarberServiceDetail
import com.example.barberapp.data.Service
import com.example.barberapp.databinding.FragmentBarberServiceBinding
import com.example.barberapp.databinding.FragmentHomeBarberBinding
import com.example.barberapp.databinding.FragmentHomeClientBinding
import com.example.barberapp.databinding.FragmentServiceItemBinding


class BarberServiceFragment : Fragment() {

    private val viewModel by viewModels<BarberServiceViewModel>()
    private lateinit var binding: FragmentBarberServiceBinding

    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBarberServiceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Obtém o barberId
        val barberId = loginViewModel.getLoggedInBarberId()
        Log.d("Barber Login", barberId.toString())

        // Carrega os serviços do barbeiro
        viewModel.loadBarberServices(barberId!!)

        binding.barberServiceList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<BarberServiceDetail, barberServiceViewHolder>(barberServiceDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): barberServiceViewHolder {
                val itemBinding = FragmentServiceItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return barberServiceViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: barberServiceViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

        // Certifique-se de que o adapter é atribuído imediatamente
        binding.barberServiceList.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.services.observe(viewLifecycleOwner) { services ->
            Log.d("ServiceFragment", "Services loaded: ${services.size}")
            adapter.submitList(services)
        }

        binding.btnCreateNewBarberService.setOnClickListener {
            findNavController().navigate(BarberServiceFragmentDirections.actionBarberServiceFragmentToCreateBarberServiceFragment())
        }
    }

    // ViewHolder interno
    inner class barberServiceViewHolder(private val binding: FragmentServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(serviceDetail: BarberServiceDetail) {
            binding.serviceNameText.text = serviceDetail.name
            Log.d("ServiceViewHolder", "ServiceNameText set to: ${serviceDetail.name}")
            binding.servicePriceText.text = serviceDetail.price.toString()// Ocultar preço (não relevante aqui)
            binding.durationText.text = serviceDetail.duration.toString()     // Ocultar duração (não relevante aqui)
        }
    }

    private val barberServiceDiffer = object : DiffUtil.ItemCallback<BarberServiceDetail>() {
        override fun areItemsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem == newItem
    }
}
