package com.example.barberapp.GalleryBarber

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.barberapp.databinding.FragmentPhotoPreviewBinding
import kotlinx.coroutines.launch
import java.io.File

class PhotoPreviewFragment : Fragment() {
    private val args by navArgs<PhotoPreviewFragmentArgs>()
    private lateinit var binding: FragmentPhotoPreviewBinding
    private val viewModel: CameraViewModel by viewModels<CameraViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhotoPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photo.load(args.file)

        binding.acceptBtn.setOnClickListener {
            /*savePhotoToGallery(args.file)*/
            viewModel.photoFile = args.file
            lifecycleScope.launch {
                viewModel.savephoto()
                findNavController().navigate(PhotoPreviewFragmentDirections.actionPhotoPreviewFragmentToGalleryFragment())
            }
        }

        binding.rejectBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /*private fun savePhotoToGallery(filePath: String) {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "barber_photo_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BarberApp")
        }

        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.also { uri ->
            resolver.openOutputStream(uri)?.use { output ->
                File(filePath).inputStream().copyTo(output)
                Toast.makeText(requireContext(), "Foto guardada!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(PhotoPreviewFragmentDirections.actionPhotoPreviewFragmentToGalleryFragment())
            }
        } ?: Toast.makeText(requireContext(), "Erro ao guardar a foto.", Toast.LENGTH_SHORT).show()
    }*/
}

