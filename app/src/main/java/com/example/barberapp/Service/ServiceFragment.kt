package com.example.barberapp.Service

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.data.Service
import com.example.barberapp.databinding.FragmentServiceBinding
import com.example.barberapp.databinding.FragmentServiceItemBinding

class ServiceFragment : Fragment() {

    private val viewModel by viewModels<ServiceViewModel>()

    private lateinit var binding: FragmentServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        binding.serviceList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<Service, ServiceViewHolder>(serviceDiffer) {
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

        // Certifique-se de que o adapter é atribuído imediatamente
        binding.serviceList.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.services.observe(viewLifecycleOwner) { services ->
            Log.d("ServiceFragment", "Services loaded: ${services.size}")
            adapter.submitList(services)
        }
    }


    // ViewHolder interno
    inner class ServiceViewHolder(private val binding: FragmentServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.serviceNameText.text = service.name
            Log.d("ServiceViewHolder", "ServiceNameText set to: ${service.name}")
            binding.servicePriceText.visibility = View.GONE // Ocultar preço (não relevante aqui)
            binding.durationText.visibility = View.GONE     // Ocultar duração (não relevante aqui)
        }
    }

    // DiffUtil para melhor desempenho
    private val serviceDiffer = object : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: Service, newItem: Service) = oldItem == newItem
        }
}
