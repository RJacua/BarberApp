package com.example.barberapp.GalleryBarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.barberapp.PhotoComments.PhotoDetailsViewModel
import com.example.barberapp.data.Rating
import com.example.barberapp.databinding.FragmentCommentItemBinding
import com.example.barberapp.databinding.FragmentPhotoDetailsBarberBinding

class PhotoDetailsBarberFragment : Fragment() {
    private val args by navArgs<PhotoDetailsBarberFragmentArgs>()
    private lateinit var binding: FragmentPhotoDetailsBarberBinding
    private val viewModel by viewModels<PhotoDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoDetailsBarberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUrl = args.photoUrl
        binding.imageView2.load(photoUrl)

        binding.commentList.layoutManager = LinearLayoutManager(requireContext())

        val adapter = object : ListAdapter<Pair<Rating, String?>, RatingViewHolder>(ratingDiffer) {
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

        binding.commentList.adapter = adapter

        viewModel.ratings.observe(viewLifecycleOwner) { ratings ->
            ratings?.let {
                adapter.submitList(it)
                val averageRating = if (it.isNotEmpty()) it.map { pair -> pair.first.rating }.average().toInt() else 0
                updateStars(averageRating)
            }
        }

        viewModel.loadRatingsByPhotoUrl(photoUrl)

        parentFragmentManager.setFragmentResultListener("update_comments", viewLifecycleOwner) { _, _ ->
            viewModel.loadRatingsByPhotoUrl(photoUrl)
        }

    }

    inner class RatingViewHolder(private val binding: FragmentCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ratingWithClient: Pair<Rating, String?>) {
            val rating = ratingWithClient.first
            val clientName = ratingWithClient.second ?: "Cliente Desconhecido"

            binding.comment.text = rating.comment
            binding.clientName.text = clientName
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

    private val ratingDiffer = object : DiffUtil.ItemCallback<Pair<Rating, String?>>() {
        override fun areItemsTheSame(oldItem: Pair<Rating, String?>, newItem: Pair<Rating, String?>) =
            oldItem.first.ratingId == newItem.first.ratingId

        override fun areContentsTheSame(oldItem: Pair<Rating, String?>, newItem: Pair<Rating, String?>) =
            oldItem == newItem
    }
}