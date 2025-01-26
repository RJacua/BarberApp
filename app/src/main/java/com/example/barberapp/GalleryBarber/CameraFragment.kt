package com.example.barberapp.GalleryBarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.barberapp.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import java.io.File

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.camera.setLifecycleOwner(viewLifecycleOwner)

        binding.shutterBtn.setOnClickListener {
            binding.camera.takePicture()
        }

        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val file = File(requireContext().filesDir, "photo_temp.jpg")

                result.toFile(file) {
                    findNavController().navigate(
                        CameraFragmentDirections.actionCameraFragmentToPhotoPreviewFragment(it!!)
                    )
                }
            }
        })
    }
}