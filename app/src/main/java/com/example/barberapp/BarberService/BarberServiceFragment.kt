package com.example.barberapp.BarberService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.Login.LoginViewModel
import com.example.barberapp.data.BarberServiceDetail
import com.example.barberapp.databinding.FragmentBarberServiceBinding
import com.example.barberapp.databinding.FragmentServiceItemBinding
import java.text.DecimalFormat

import kotlin.math.round

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

        binding.btnBackBarbServToHome.setOnClickListener {
            findNavController().navigate(BarberServiceFragmentDirections.actionBarberServiceFragmentToHomeBarberFragment())
        }
    }

    // ViewHolder interno
    inner class barberServiceViewHolder(private val binding: FragmentServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(serviceDetail: BarberServiceDetail) {
            var h = ""
            var m = ""
            var n = ""
            if(serviceDetail.duration.toString().split(":")[0].toInt() != 0) {
                h = serviceDetail.duration.toString().split(":")[0].replace("0", "") + "h"
            }
            if(serviceDetail.duration.toString().split(":")[1].toInt() != 0) {
                m = serviceDetail.duration.toString().split(":")[1] + "min"
            }
            if(serviceDetail.duration.toString().split(":")[0].toInt() != 0 && serviceDetail.duration.toString().split(":")[1].toInt() != 0) n = "and";
            var duration = "${h} ${n} ${m}"

            var price = serviceDetail.price
            val decimalFormat = DecimalFormat("#.00") // Garante duas casas decimais
            val stringPrice = decimalFormat.format(price)

            binding.serviceNameText.text = serviceDetail.name
            binding.servicePriceText.text = "Price: €" + stringPrice// Ocultar preço (não relevante aqui)
            binding.durationText.text = "Duration: " + duration
        }
    }

    private val barberServiceDiffer = object : DiffUtil.ItemCallback<BarberServiceDetail>() {
        override fun areItemsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem == newItem
    }
}
