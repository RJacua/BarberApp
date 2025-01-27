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
    ): View {
        binding = FragmentGalleryClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // set up drop down
        setupSpinner()

        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            Log.d("ClientGalleryFragment", "Fotos carregadas: ${photos.size} fotos.")
            adapter.submitList(photos)
        }

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    inner class GalleryViewHolder(private val binding: FragmentPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Pair<String, String>) {
            // load image
            binding.photoImageView.load(photo.first)
            // define barber name
            binding.textBarber.text = photo.second
        }
    }

    /**
     * Load photos filtered by barbershop id
     *
     */
    private fun loadPhotos() {
        val barbershopId = selectedBarbershopId
        if (barbershopId != null) {
            Log.d("ClientGalleryFragment", "Carregando fotos para a barbearia ID: $barbershopId")

            val photos = requireContext().filesDir.listFiles()?.filter { file ->
                // filter photos based on barbershop id
                val matches = file.nameWithoutExtension.matches(Regex("${barbershopId}_\\d+_\\w+"))
                if (matches) {
                    Log.d("ClientGalleryFragment", "Foto encontrada: ${file.name}")
                }
                matches
            }?.map { file ->
                // get barber id from file name
                val fileNameParts = file.nameWithoutExtension.split("_")
                val barberId = fileNameParts.getOrNull(1)?.toIntOrNull()

                // verify if barber id was found
                if (barberId != null) {
                    // get barber name from the data base
                    val barberName = "Barber Name: " + viewModel.getBarberNameById(barberId)
                    // return file path and barber name
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

    /**
     * Setup spinner with barbershops
     *
     */
    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>() // initial empty list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.BarberShopList.adapter = adapter

        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            if (!barbershops.isNullOrEmpty()) {
                val barberShopNames = barbershops.map { it.name } // get barbershop names
                adapter.clear()
                adapter.addAll(barberShopNames)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "No barbershop found", Toast.LENGTH_SHORT).show()
            }
        }

        // filter photos on barbershop select
        binding.BarberShopList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * On item selected
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBarbershopId = viewModel.barbershops.value?.get(position)?.barbershopId
                loadPhotos() // load filtered photos for selected barbershop
            }

            /**
             * On nothing selected
             *
             * @param parent
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBarbershopId = null
                viewModel.setPhotos(emptyList()) // clean photos if no barbershop was selected
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
