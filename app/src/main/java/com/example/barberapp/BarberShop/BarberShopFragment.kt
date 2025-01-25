package com.example.barberapp.BarberShop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.data.Barbershop
import com.example.barberapp.databinding.FragmentBarberShopBinding
import com.example.barberapp.databinding.FragmentBarberShopItemBinding
import com.example.barberapp.UserSession

class BarberShopFragment : Fragment() {

    private val viewModel by viewModels<BarbershopViewModel>()

    private lateinit var binding: FragmentBarberShopBinding

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // Variável temporária para armazenar a barbearia selecionada
    private var selectedBarbershop: Barbershop? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarberShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar o RecyclerView
        binding.barberShopList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<Barbershop, BarberShopViewHolder>(barbershopDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberShopViewHolder {
                val itemBinding = FragmentBarberShopItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BarberShopViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: BarberShopViewHolder, position: Int) {
                holder.bind(getItem(position), position == selectedPosition)
            }
        }
        binding.barberShopList.adapter = adapter

        // Observar os dados do ViewModel e atualizá-los no RecyclerView
        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            // Verificar se há um ID de barbearia previamente selecionado
            val savedId = UserSession.selectedBarberShopId
            if (savedId != null) {
                // Encontrar a posição correspondente
                selectedPosition = barbershops.indexOfFirst { it.barbershopId == savedId }
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    selectedBarbershop = barbershops[selectedPosition]
                }
            }
            adapter.submitList(barbershops)
        }

        binding.savebtn.setOnClickListener {
            if (selectedBarbershop != null) {
                // Salvar no UserSession
                UserSession.selectedBarberShopId = selectedBarbershop?.barbershopId
//                Log.d(
//                    "BarberShopFragment",
//                    "Barbearia salva: ${selectedBarbershop?.barbershopId} - ${selectedBarbershop?.name}"
//                )
//                Toast.makeText(
//                    requireContext(),
//                    "Barbearia salva: ${selectedBarbershop?.name}",
//                    Toast.LENGTH_SHORT
//                ).show()

                // Retornar ao fragmento anterior
                UserSession.selectedBarberId = null
                parentFragmentManager.popBackStack()
            } else {
                // Avisar que nenhuma barbearia foi selecionada
                Toast.makeText(
                    requireContext(),
                    "Selecione uma barbearia antes de salvar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ViewHolder interno
    inner class BarberShopViewHolder(private val binding: FragmentBarberShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barbershop: Barbershop, isSelected: Boolean) {
            binding.barberShopName.text = barbershop.name
            binding.barberShopAddress.text = barbershop.address
            binding.idText.text = barbershop.barbershopId.toString()

            // Atualiza a seleção visual
            binding.itemContainer.isSelected = isSelected

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                // Atualiza a barbearia selecionada
                selectedBarbershop = barbershop

                // Notificar alterações visuais
                val adapter = binding.root.parent as? RecyclerView
                if (previousPosition != RecyclerView.NO_POSITION) {
                    adapter?.adapter?.notifyItemChanged(previousPosition)
                }
                adapter?.adapter?.notifyItemChanged(selectedPosition)

                Log.d("BarberShopFragment", "Selecionado: ${barbershop.name}")
            }
        }
    }

    // DiffUtil para melhor desempenho
    private val barbershopDiffer = object : DiffUtil.ItemCallback<Barbershop>() {
        override fun areItemsTheSame(oldItem: Barbershop, newItem: Barbershop) =
            oldItem.barbershopId == newItem.barbershopId

        override fun areContentsTheSame(oldItem: Barbershop, newItem: Barbershop) = oldItem == newItem
    }
}