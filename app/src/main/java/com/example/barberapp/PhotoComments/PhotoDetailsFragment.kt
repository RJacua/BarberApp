package com.example.barberapp.PhotoComments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.barberapp.data.Rating
import com.example.barberapp.databinding.FragmentCommentItemBinding
import com.example.barberapp.databinding.FragmentPhotoDetailsBinding

class PhotoDetailsFragment : Fragment() {

    private val args by navArgs<PhotoDetailsFragmentArgs>()
    private lateinit var binding: FragmentPhotoDetailsBinding
    private val viewModel by viewModels<PhotoDetailsViewModel>()  // ViewModel para ratings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUrl = args.photoUrl
        binding.imageView2.load(photoUrl) // Exibe a imagem recebida

        // Configuração do layout manager
        binding.commentList.layoutManager = LinearLayoutManager(requireContext())

        // Criando o adapter
        val adapter = object : ListAdapter<Rating, RatingViewHolder>(ratingDiffer) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
                val itemBinding = FragmentCommentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return RatingViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

        binding.commentList.adapter = adapter  // Associando o adapter à RecyclerView

        viewModel.ratings.observe(viewLifecycleOwner) { ratings ->
            ratings?.let {
                adapter.submitList(it)  // Envia a lista de comentários atualizada para o adapter
                val averageRating = it.map { rating -> rating.rating }.average().toInt()
                updateStars(averageRating)
            }
        }



        // Carregar os ratings pela URL da foto
        viewModel.loadRatingsByPhotoUrl(photoUrl)

        // **Escutar a atualização dos comentários**
        parentFragmentManager.setFragmentResultListener("update_comments", viewLifecycleOwner) { _, bundle ->
            val photoUrl = args.photoUrl
            viewModel.loadRatingsByPhotoUrl(photoUrl) // Atualiza os ratings
        }

        binding.button2.setOnClickListener {
            findNavController().popBackStack()
        }

        // Desabilitar a interação com as estrelas
        val stars = listOf(
            binding.imageButton,
            binding.imageButton2,
            binding.imageButton3,
            binding.imageButton4,
            binding.imageButton5
        )

        // Configurar clique para cada estrela (não faz nada agora)
        for (star in stars) {
            star.isClickable = false  // Desabilitar clique nas estrelas
        }

        binding.btnAddComment.setOnClickListener {
            val modalFragment = CommentFragment()

            // Criar um Bundle e passar o URL da foto
            val bundle = Bundle().apply {
                putString("photoUrl", args.photoUrl)
            }

            // Definir os argumentos para o Fragment
            modalFragment.arguments = bundle

            // Exibir o fragmento
            modalFragment.show(parentFragmentManager, "ModalFragment")
        }
    }

    inner class RatingViewHolder(private val binding: FragmentCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: Rating) {
            binding.comment.text = rating.comment
            binding.clientName.text = "Client ID: ${rating.clientId}" // Exibindo o ID do cliente
            binding.rating.text = rating.rating.toString()
        }
    }

    private fun updateStars(averageRating: Int) {
        val stars = listOf(
            binding.imageButton,
            binding.imageButton2,
            binding.imageButton3,
            binding.imageButton4,
            binding.imageButton5
        )

        for (i in stars.indices) {
            if (i < averageRating) {
                stars[i].setImageResource(android.R.drawable.btn_star_big_on) // Estrela cheia
            } else {
                stars[i].setImageResource(android.R.drawable.btn_star_big_off) // Estrela vazia
            }
        }
    }

    private val ratingDiffer = object : DiffUtil.ItemCallback<Rating>() {
        override fun areItemsTheSame(oldItem: Rating, newItem: Rating) = oldItem.ratingId == newItem.ratingId
        override fun areContentsTheSame(oldItem: Rating, newItem: Rating) = oldItem == newItem
    }
}