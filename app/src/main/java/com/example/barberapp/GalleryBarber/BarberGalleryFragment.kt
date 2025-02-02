package com.example.barberapp.GalleryBarber

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.barberapp.UserSession
import com.example.barberapp.databinding.FragmentGalleryBinding
import com.example.barberapp.databinding.FragmentPhotoItemBinding

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: BarberGalleryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        val adapter = object : ListAdapter<String, PhotoViewHolder>(PhotoDiffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
                val itemBinding = FragmentPhotoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PhotoViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

        binding.galleryRecyclerView.adapter = adapter

        // Carregar fotos automaticamente
        loadPhotos()

        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            Log.d("GalleryFragment", "Fotos observadas: $photos")
            adapter.submitList(photos)
        }

        binding.loadPhotosBtn.setOnClickListener {
            findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment())
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToHomeBarberFragment())
        }
    }

    /**
     * Carrega as fotos do diretório filtradas pelo barberId.
     */
    private fun loadPhotos() {
        val barberId = UserSession.loggedInBarber!!.barberId
        val photos = requireContext().filesDir.listFiles()?.filter { file ->
            Log.d("GalleryFragment", "Verificando arquivo: ${file.name}")
            file.nameWithoutExtension.matches(Regex("\\d+_${barberId}_\\d+"))
        }?.map { it.absolutePath } ?: emptyList()

        Log.d("GalleryFragment", "Fotos encontradas para barberId $barberId: $photos")
        viewModel.setPhotos(photos)
    }

    inner class PhotoViewHolder(private val binding: FragmentPhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoPath: String) {
            binding.photoImageView.load(photoPath)
            Log.d("PhotoAdapter", "Carregando imagem: $photoPath")

            binding.root.setOnClickListener {
                findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToPhotoDetailsBarberFragment(photoPath))
            }
        }
    }

    private val PhotoDiffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}
