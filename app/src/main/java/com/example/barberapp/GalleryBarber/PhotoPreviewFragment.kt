package com.example.barberapp.GalleryBarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.barberapp.databinding.FragmentPhotoPreviewBinding


class PhotoPreviewFragment : Fragment() {
    private val args by navArgs<PhotoPreviewFragmentArgs>()
    //private val viewModel: CreateEditContactViewModel by navGraphViewModels(R.id.createEditContactFragment)
    private lateinit var binding: FragmentPhotoPreviewBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotoPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photo.load(args.file)

        binding.acceptBtn.setOnClickListener {
            //viewModel.photoFile = args.file

            //findNavController().popBackStack(R.id.createEditContactFragment, false)
        }

        binding.rejectBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}