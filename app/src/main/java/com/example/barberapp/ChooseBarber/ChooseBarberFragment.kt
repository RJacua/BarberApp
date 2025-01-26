package com.example.barberapp.ChooseBarber

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.UserSession
import com.example.barberapp.data.Barber
import com.example.barberapp.databinding.FragmentBarberBinding
import com.example.barberapp.databinding.FragmentBarberItemBinding

class ChooseBarberFragment : Fragment() {

    private val viewModel by viewModels<ChooseBarberViewModel>()

    private lateinit var binding: FragmentBarberBinding

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // Variável temporária para armazenar a barbeiro selecionada
    private var selectedBarber: Barber? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configurar o View Binding para o fragmento
        binding = FragmentBarberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        binding.barberList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<Barber, BarberViewHolder>(barberDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
                val itemBinding = FragmentBarberItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return BarberViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
                holder.bind(getItem(position), position == selectedPosition)
            }

        }

        // Certifique-se de que o adapter é atribuído imediatamente
        binding.barberList.adapter = adapter

        // Observar os dados do ViewModel
        viewModel.barbers.observe(viewLifecycleOwner) { barbers ->
            // Verificar se há um ID de barbeiro previamente selecionado
            val savedId = UserSession.selectedBarberId
            if (savedId != null) {
                // Encontrar a posição correspondente
                selectedPosition = barbers.indexOfFirst { it.barberId == savedId }
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    selectedBarber = barbers[selectedPosition]
                }
            }
            adapter.submitList(barbers)
        }

        binding.savebtn.setOnClickListener {
            if (selectedBarber != null) {
                // Salvar no UserSession
                UserSession.selectedBarberId = selectedBarber?.barberId
                // Retornar ao fragmento anterior
                UserSession.selectedServiceIds.clear()
                UserSession.selectedAppointmentTime = null
                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "Selecione um barbeiro antes de salvar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.backBtn.setOnClickListener{
            findNavController().navigateUp() // Volta para o fragmento anterior
        }

    }

    // ViewHolder interno
    inner class BarberViewHolder(private val binding: FragmentBarberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barber: Barber, isSelected: Boolean) {
            binding.barberName.text = barber.name
            Log.d("BarberViewHolder", "barberName set to: ${barber.name}")

            // Atualiza a seleção visual
            binding.itemContainerBarber.isSelected = isSelected

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                // Atualiza o barbeiro selecionada
                selectedBarber = barber

                // Notificar alterações visuais
                val adapter = binding.root.parent as? RecyclerView
                if (previousPosition != RecyclerView.NO_POSITION) {
                    adapter?.adapter?.notifyItemChanged(previousPosition)
                }
                adapter?.adapter?.notifyItemChanged(selectedPosition)

                Log.d("BarberFragment", "Selecionado: ${barber.name}")
            }
        }
    }

    // DiffUtil para melhor desempenho
    private val barberDiffer = object : DiffUtil.ItemCallback<Barber>() {
        override fun areItemsTheSame(oldItem: Barber, newItem: Barber) = oldItem.barberId == newItem.barberId
        override fun areContentsTheSame(oldItem: Barber, newItem: Barber) = oldItem == newItem
    }

}