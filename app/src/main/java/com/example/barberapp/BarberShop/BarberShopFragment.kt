package com.example.barberapp.BarberShop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.data.Barbershop
import com.example.barberapp.databinding.FragmentBarberShopBinding
import com.example.barberapp.databinding.FragmentBarberShopItemBinding

class BarberShopFragment : Fragment() {

    private val viewModel by viewModels<BarbershopViewModel>()

    private lateinit var binding: FragmentBarberShopBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configurar o View Binding para o fragmento
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
                holder.bind(getItem(position))
            }
        }
        binding.barberShopList.adapter = adapter

        // Observar os dados do ViewModel e atualizá-los no RecyclerView
        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            adapter.submitList(barbershops)
        }
    }

    // ViewHolder interno
    inner class BarberShopViewHolder(private val binding: FragmentBarberShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barbershop: Barbershop) {
            binding.barberShopNameText.text = barbershop.name
            binding.addressText.text = barbershop.address
            binding.idText.text = barbershop.barbershopId.toString()
            //binding.textViewRating.text = "Rating: ${barbershop.rating}"
        }
    }

    // DiffUtil para melhor desempenho
    private val barbershopDiffer = object : DiffUtil.ItemCallback<Barbershop>() {
        override fun areItemsTheSame(oldItem: Barbershop, newItem: Barbershop) = oldItem.barbershopId == newItem.barbershopId
        override fun areContentsTheSame(oldItem: Barbershop, newItem: Barbershop) = oldItem == newItem
    }
}
