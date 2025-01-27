package com.example.barberapp.ChooseBarberShop

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
import com.example.barberapp.data.Barbershop
import com.example.barberapp.databinding.FragmentBarberShopBinding
import com.example.barberapp.databinding.FragmentBarberShopItemBinding
import com.example.barberapp.UserSession

class ChooseBarberShopFragment : Fragment() {

    private val viewModel by viewModels<BarbershopViewModel>()

    private lateinit var binding: FragmentBarberShopBinding

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // temp variable to store selected barbershop
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

        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            // verify if a barbershop was already selected
            val savedId = UserSession.selectedBarberShopId
            if (savedId != null) {
                // find corresponding position
                selectedPosition = barbershops.indexOfFirst { it.barbershopId == savedId }
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    selectedBarbershop = barbershops[selectedPosition]
                }
            }
            adapter.submitList(barbershops)
        }

        binding.savebtn.setOnClickListener {
            if (selectedBarbershop != null) {
                // save to UserSession
                UserSession.selectedBarberShopId = selectedBarbershop?.barbershopId

                // set all next ids as null
                UserSession.selectedBarberId = null
                UserSession.selectedServiceIds.clear()
                UserSession.selectedAppointmentTime = null
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select a barbershop.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.backBtn.setOnClickListener{
            findNavController().navigateUp()
        }
    }


    inner class BarberShopViewHolder(private val binding: FragmentBarberShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barbershop: Barbershop, isSelected: Boolean) {
            binding.barberShopName.text = barbershop.name
            binding.barberShopAddress.text = barbershop.address
            binding.idText.text = barbershop.barbershopId.toString()

            // update visual selection
            binding.itemContainer.isSelected = isSelected

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                // update selected barbershop
                selectedBarbershop = barbershop

                val adapter = binding.root.parent as? RecyclerView
                if (previousPosition != RecyclerView.NO_POSITION) {
                    adapter?.adapter?.notifyItemChanged(previousPosition)
                }
                adapter?.adapter?.notifyItemChanged(selectedPosition)

                Log.d("BarberShopFragment", "Selecionado: ${barbershop.name}")
            }
        }
    }

    private val barbershopDiffer = object : DiffUtil.ItemCallback<Barbershop>() {
        override fun areItemsTheSame(oldItem: Barbershop, newItem: Barbershop) =
            oldItem.barbershopId == newItem.barbershopId

        override fun areContentsTheSame(oldItem: Barbershop, newItem: Barbershop) = oldItem == newItem
    }
}