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
    private val viewModel by viewModels<PhotoDetailsViewModel>()
    private val args by navArgs<CommentFragmentArgs>()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCommentBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelar.setOnClickListener {
            dismiss() // Fecha o modal
        }

    binding.btnSalvar.setOnClickListener {
        val comentario = binding.etComentario.text.toString()
        val rating = binding.ratingBar.rating

        if (comentario.isNotBlank()) {
            val novoRating = Rating(
                clientId = UserSession.loggedInClient!!.clientId,
                photoUrl = args.photoUrl,
                rating = rating.toDouble(),
                comment = comentario
            )
            viewModel.insertRating(novoRating)
            viewModel.loadRatingsByPhotoUrl(args.photoUrl)

            // **Notificar PhotoDetailsFragment que um comentário foi adicionado**
            parentFragmentManager.setFragmentResult("update_comments", bundleOf())

            dismiss()
        } else {
            binding.etComentario.error = "Write a comment!"
        }
    }

    return dialog
}

}