package com.example.barberapp.GalleryBarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.barberapp.databinding.FragmentPhotoPreviewBinding
import kotlinx.coroutines.launch


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
}

