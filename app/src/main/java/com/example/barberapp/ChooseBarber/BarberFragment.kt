package com.example.barberapp.ChooseBarber

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.data.Barber
import com.example.barberapp.databinding.FragmentBarberBinding
import com.example.barberapp.databinding.FragmentBarberItemBinding

class BarberFragment : Fragment() {

    private val viewModel by viewModels<BarberViewModel>()

    private lateinit var binding: FragmentBarberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configurar o View Binding para o fragmento
        binding = FragmentBarberBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Configurar RecyclerView
//        binding.barberList.layoutManager = LinearLayoutManager(requireContext())
//        val adapter = object : ListAdapter<Barber, BarberViewHolder>(barberDiffer) {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
//                val itemBinding = FragmentBarberItemBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                return BarberViewHolder(itemBinding)
//            }
//
//            override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
//                holder.bind(getItem(position))
//            }
//        }
//
//        // Certifique-se de que o adapter é atribuído imediatamente
//        binding.barberList.adapter = adapter
//
//        // Observar os dados do ViewModel
//        viewModel.barbers.observe(viewLifecycleOwner) { barbers ->
//            Log.d("BarberFragment", "Barbers loaded: ${barbers.size}")
//            adapter.submitList(barbers)
//        }
//    }
//
//
//    // ViewHolder interno
//    inner class BarberViewHolder(private val binding: FragmentBarberItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(barber: Barber) {
//            binding.barberName.text = barber.name
//            Log.d("BarberViewHolder", "barberName set to: ${barber.name}")
//        }
//    }
//
//    // DiffUtil para melhor desempenho
//    private val barberDiffer = object : DiffUtil.ItemCallback<Barber>() {
//        override fun areItemsTheSame(oldItem:Barber, newItem: Barber) = oldItem.barberId == newItem.barberId
//        override fun areContentsTheSame(oldItem: Barber, newItem: Barber) = oldItem == newItem
//    }

}