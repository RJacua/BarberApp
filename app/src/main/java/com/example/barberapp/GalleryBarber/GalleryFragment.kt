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
import com.example.barberapp.databinding.PhotoItemBinding

class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuração do RecyclerView para exibir fotos em 3 colunas
        binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = PhotoAdapter()
        binding.galleryRecyclerView.adapter = adapter

        // Carregar todas as fotos automaticamente
        loadPhotos()

        // Observando as fotos no ViewModel
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            Log.d("GalleryFragment", "Fotos observadas: $photos")
            adapter.submitList(photos)
        }

        binding.loadPhotosBtn.setOnClickListener{
            findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment())
        }
    }


    // Função para carregar as fotos do diretório
    // Função para carregar as fotos do diretório filtrando pelo barberId
    private fun loadPhotos() {
        val barberId = UserSession.loggedInBarber!!.barberId
        val photos = requireContext().filesDir.listFiles()?.filter { file ->
            Log.d("GalleryFragment", "Verificando arquivo: ${file.name}")
            // Verificando se o nome do arquivo contém o barberId
            file.nameWithoutExtension.matches(Regex("\\d+_${barberId}_\\d+"))
        }?.map { it.absolutePath } ?: emptyList()

        Log.d("GalleryFragment", "Fotos encontradas para o barberId $barberId: $photos")
        viewModel.setPhotos(photos)
    }


}

class PhotoAdapter : ListAdapter<String, PhotoAdapter.PhotoViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoViewHolder(private val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoPath: String) {
            binding.photoImageView.load(photoPath)
            Log.d("PhotoAdapter", "Carregando imagem: $photoPath")
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}
