package com.example.barberapp.SetBarberService

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
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
import com.example.barberapp.R
import com.example.barberapp.UserSession
import com.example.barberapp.UtilityClasses.BarberServiceDetail
import com.example.barberapp.databinding.FragmentBarberServiceBinding
import com.example.barberapp.databinding.FragmentBarberServiceItemBinding
import java.text.DecimalFormat

class BarberServiceFragment : Fragment() {

    private val viewModel by viewModels<BarberServiceViewModel>()
    private lateinit var binding: FragmentBarberServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBarberServiceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Obtém o barberId
        val barberId = UserSession.loggedInBarber?.barberId;
        Log.d("Barber Login", barberId.toString())

        // Carrega os serviços do barbeiro
        viewModel.loadBarberServices(barberId!!)

        binding.barberServiceList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<BarberServiceDetail, BarberServiceViewHolder>(barberServiceDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberServiceViewHolder {
                val itemBinding = FragmentBarberServiceItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BarberServiceViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: BarberServiceViewHolder, position: Int) {
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

        binding.btnBackBarbServToHome.setOnClickListener {
            findNavController().navigate(BarberServiceFragmentDirections.actionBarberServiceFragmentToHomeBarberFragment())
        }
    }

    // ViewHolder interno
    inner class BarberServiceViewHolder(private val binding: FragmentBarberServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(serviceDetail: BarberServiceDetail) {
            var h = ""
            var m = ""
            var n = ""
            if (serviceDetail.duration.toString().split(":")[0].toInt() != 0) {
                h = serviceDetail.duration.toString().split(":")[0].replace("0", "") + "h"
            }
            if (serviceDetail.duration.toString().split(":")[1].toInt() != 0) {
                m = serviceDetail.duration.toString().split(":")[1] + "min"
            }
            if (serviceDetail.duration.toString().split(":")[0].toInt() != 0 &&
                serviceDetail.duration.toString().split(":")[1].toInt() != 0
            ) n = "and"
            val duration = "$h $n $m"

            val price = serviceDetail.price
            val decimalFormat = DecimalFormat("#.00") // Garante duas casas decimais
            val stringPrice = decimalFormat.format(price)

            // Define estilos com base em "isActive"
            if (!serviceDetail.isActive) {
                binding.serviceNameText.setTextColor(Color.BLACK)
                binding.serviceNameText.setTypeface(null, Typeface.BOLD)
                binding.servicePriceText.setTextColor(Color.BLACK)
                binding.servicePriceText.setTypeface(null, Typeface.BOLD)
                binding.durationText.setTextColor(Color.BLACK)
                binding.durationText.setTypeface(null, Typeface.BOLD)
            }

            // Define os textos dos serviços
            binding.serviceNameText.text = serviceDetail.name
            val priceText = getString(R.string.price_text, stringPrice)
            val durationText = getString(R.string.duration_text, duration)

            binding.servicePriceText.text = priceText
            binding.durationText.text = durationText

            // Configura o botão Edit (sempre igual para ativos e inativos)
            binding.btnEditService.setOnClickListener {
                val action = BarberServiceFragmentDirections
                    .actionBarberServiceFragmentToCreateBarberServiceFragment(
                        serviceId = serviceDetail.serviceId
                    )
                findNavController().navigate(action)
            }
        }
    }



    private val barberServiceDiffer = object : DiffUtil.ItemCallback<BarberServiceDetail>() {
        override fun areItemsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem == newItem
    }
}
