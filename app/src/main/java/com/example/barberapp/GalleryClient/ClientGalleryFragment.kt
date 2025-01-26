package com.example.barberapp.GalleryClient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.barberapp.databinding.FragmentGalleryClientBinding
import com.example.barberapp.databinding.FragmentPhotoItemBinding

class ClientGalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryClientBinding
    private val viewModel: ClientGalleryViewModel by viewModels()
    private var selectedBarbershopId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGalleryClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuração do RecyclerView
        binding.galleryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = object : ListAdapter<Pair<String, String>, GalleryViewHolder>(photosDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
                val itemBinding = FragmentPhotoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return GalleryViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

        binding.galleryRecyclerView.adapter = adapter

        // Configurar o spinner
        setupSpinner()

        // Observar as fotos no ViewModel
        // Observar as fotos no ViewModel
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            Log.d("ClientGalleryFragment", "Fotos carregadas: ${photos.size} fotos.")
            // Atualiza o RecyclerView quando as fotos são carregadas
            adapter.submitList(photos)
        }

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    inner class GalleryViewHolder(private val binding: FragmentPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Pair<String, String>) {
            // Carregar a imagem
            binding.photoImageView.load(photo.first)
            // Definir o nome do barbeiro
            binding.textBarber.text = photo.second // Assumindo que você tenha um TextView para o nome
        }
    }


    // Função para carregar as fotos filtrando pelo barbershopId
    private fun loadPhotos() {
        val barbershopId = selectedBarbershopId
        if (barbershopId != null) {
            Log.d("ClientGalleryFragment", "Carregando fotos para a barbearia ID: $barbershopId")

            val photos = requireContext().filesDir.listFiles()?.filter { file ->
                // Filtra as fotos com base no barbershopId
                val matches = file.nameWithoutExtension.matches(Regex("${barbershopId}_\\d+_\\w+"))
                if (matches) {
                    Log.d("ClientGalleryFragment", "Foto encontrada: ${file.name}")
                }
                matches
            }?.map { file ->
                // Extrair o idBarber do nome do arquivo
                val fileNameParts = file.nameWithoutExtension.split("_")
                val barberId = fileNameParts.getOrNull(1)?.toIntOrNull()

                // Verifica se o idBarber foi encontrado
                if (barberId != null) {
                    // Buscar o nome do barbeiro no banco de dados
                    val barberName = "Barber Name: " + viewModel.getBarberNameById(barberId)
                    // Retornar o caminho da foto e o nome do barbeiro
                    Pair(file.absolutePath, barberName)
                } else {
                    null
                }
            }?.filterNotNull() ?: emptyList()

            Log.d("ClientGalleryFragment", "Número de fotos encontradas: ${photos.size}")
            viewModel.setPhotos(photos)
        } else {
            Log.d("ClientGalleryFragment", "Nenhuma barbearia selecionada.")
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>() // Lista vazia inicial
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.BarberShopList.adapter = adapter

        // Observar os dados das barbearias no ViewModel
        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            if (!barbershops.isNullOrEmpty()) {
                val barberShopNames = barbershops.map { it.name } // Obter os nomes
                adapter.clear()
                adapter.addAll(barberShopNames)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Nenhuma barbearia encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        // Quando uma barbearia for selecionada, filtrar as fotos
        binding.BarberShopList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBarbershopId = viewModel.barbershops.value?.get(position)?.barbershopId
                loadPhotos() // Carregar as fotos para a barbearia selecionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBarbershopId = null
                viewModel.setPhotos(emptyList()) // Limpar as fotos se nenhuma barbearia for selecionada
            }
        }
    }

    private val photosDiffer = object : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(oldItem: Pair<String, String>, newItem: Pair<String, String>) =
            oldItem.first == newItem.first

        override fun areContentsTheSame(oldItem: Pair<String, String>, newItem: Pair<String, String>) =
            oldItem == newItem
    }
}
