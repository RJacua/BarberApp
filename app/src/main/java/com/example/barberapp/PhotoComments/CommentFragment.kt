package com.example.barberapp.PhotoComments

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.barberapp.UserSession
import com.example.barberapp.data.Rating
import com.example.barberapp.databinding.FragmentCommentBinding

class CommentFragment : DialogFragment() {

    private lateinit var binding: FragmentCommentBinding
    private val viewModel by viewModels<CommentViewModel>()
    private val args by navArgs<CommentFragmentArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCommentBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Set background to transparent for a cleaner look

        binding.btnCancelar.setOnClickListener {
            dismiss() // Close the dialog when cancel button is clicked
        }

        binding.btnSalvar.setOnClickListener {
            val comentario = binding.etComentario.text.toString()
            val rating = binding.ratingBar.rating

            if (comentario.isNotBlank()) { // Ensure the comment is not empty before saving
                val novoRating = Rating(
                    clientId = UserSession.loggedInClient!!.clientId, // Get the logged-in client's ID (ensure it's not null to avoid crashes)
                    photoUrl = args.photoUrl, // Assign the photo URL to the rating
                    rating = rating.toDouble(), // Convert rating to Double
                    comment = comentario // Set the user's comment
                )

                viewModel.insertRating(novoRating) // Save the new rating in ViewModel

                // **Wait 200ms before closing to ensure the update is applied**
                binding.btnSalvar.postDelayed({
                    parentFragmentManager.setFragmentResult("update_comments", bundleOf()) // Notify other fragments to update comments list
                    dismiss() // Close the dialog
                }, 200)
            } else {
                binding.etComentario.error = "Write a comment!" // Display error if the comment field is empty
            }
        }

        return dialog
    }
}

