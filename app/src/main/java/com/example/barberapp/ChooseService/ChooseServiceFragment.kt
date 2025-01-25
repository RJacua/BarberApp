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
import com.example.barberapp.BarberService.BarberServiceViewModel
import com.example.barberapp.UserSession
import com.example.barberapp.data.BarberServiceDetail
import com.example.barberapp.data.Service
import com.example.barberapp.databinding.FragmentChooseServiceBinding
import com.example.barberapp.databinding.FragmentServiceItemBinding
import java.text.DecimalFormat

class ChooseServiceFragment : Fragment() {

    private val viewModel by viewModels<ChooseServiceViewModel>()
    private val viewModelBarberService by viewModels<BarberServiceViewModel>()
    private lateinit var binding: FragmentChooseServiceBinding

    // Lista de IDs dos serviços selecionados
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
            // Pode ser útil mostrar uma mensagem de erro ao usuário ou lidar com isso adequadamente.
        }

        // Configurar RecyclerView
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

        // Certifique-se de que o adapter é atribuído imediatamente
        binding.serviceList.adapter = adapter

        // Observar os dados do ViewModel
//        viewModelBarberService.services.observe(viewLifecycleOwner) { services ->
//            Log.d("ServiceFragment", "Services loaded: ${services.size}")
//
//            // Atualizar os serviços já selecionados na lista
//            selectedServiceIds.clear()
//            selectedServiceIds.addAll(UserSession.selectedServiceIds) // Preencher com os valores da variável global
//
//            adapter.submitList(services)
//        }

        viewModelBarberService.services.observe(viewLifecycleOwner) { barberServices ->
            Log.d("ChooseServiceFragment", "Barber services: $barberServices")

            // Ordena os serviços pelos ativos primeiro
            val displayItems = barberServices.sortedByDescending { it.isActive }

            // Atualizar os serviços já selecionados na lista
            selectedServiceIds.clear()
            selectedServiceIds.addAll(UserSession.selectedServiceIds) // Preencher com os valores da variável global

            // Submete a lista ordenada ao adapter
            adapter.submitList(displayItems)

            // Forçar atualização visual do RecyclerView
            binding.serviceList.post { adapter.notifyDataSetChanged() }
        }


        // Botão para confirmar os serviços selecionados
        binding.savebtn.setOnClickListener {
            if (selectedServiceIds.isNotEmpty()) {
                // Salvar no UserSession
                UserSession.selectedServiceIds.clear()
                UserSession.selectedServiceIds.addAll(selectedServiceIds)

                //Dar clear na variável global appoitment
                UserSession.selectedAppointmentTime = null
                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "Selecione serviços antes de salvar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.backBtn.setOnClickListener{
            findNavController().navigateUp() // Volta para o fragmento anterior
        }
    }

    // ViewHolder interno
    inner class ServiceViewHolder(private val binding: FragmentServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(serviceDetail: BarberServiceDetail) {
            var stringPrice = "00.00"  // Definir um valor padrão
            var duration = "00h00min"  // Definir um valor padrão

            // Verifique se price e duration não são null
            val isAvailable = serviceDetail.isActive
            if (isAvailable) {
                var h = ""
                var m = ""
                var n = ""

                // Lógica para manipulação de duration
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

                // Formatação do preço
                val decimalFormat = DecimalFormat("#.00") // Garante duas casas decimais
                stringPrice = decimalFormat.format(serviceDetail.price)
            }

            // Atualizar a interface com os valores processados
            binding.serviceNameText.text = serviceDetail.name
            binding.servicePriceText.text = "Price: €$stringPrice"
            binding.durationText.text = "Duration: $duration"

            // Define se o item está ativo ou inativo com base nos valores de preço e duração
            binding.root.isEnabled = isAvailable
            // Ajuste no alpha para garantir que a opacidade seja aplicada corretamente
            binding.root.alpha = if (isAvailable) 1f else 0.5f

            // Atualizar visual com base na seleção
            binding.itemContainerService.isSelected =
                selectedServiceIds.contains(serviceDetail.serviceId)

            // Clique para adicionar/remover da seleção
            binding.root.setOnClickListener {
                if (selectedServiceIds.contains(serviceDetail.serviceId)) {
                    selectedServiceIds.remove(serviceDetail.serviceId) // Remover da seleção
                } else {
                    selectedServiceIds.add(serviceDetail.serviceId) // Adicionar à seleção
                }

                // Atualizar o visual do item
                binding.itemContainerService.isSelected =
                    selectedServiceIds.contains(serviceDetail.serviceId)
            }
        }
    }

    // DiffUtil para melhor desempenho
    private val serviceDiffer = object : DiffUtil.ItemCallback<BarberServiceDetail>() {
        override fun areItemsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: BarberServiceDetail, newItem: BarberServiceDetail) = oldItem == newItem
    }
}
