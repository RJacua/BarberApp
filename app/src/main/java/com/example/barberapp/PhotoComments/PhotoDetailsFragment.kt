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
    private val viewModel by viewModels<PhotoDetailsViewModel>()

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
        binding.imageView2.load(photoUrl) // Load the image from the given URL

        binding.commentList.layoutManager = LinearLayoutManager(requireContext()) // Set up RecyclerView layout

        // Adapter to display comments with ratings
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

        binding.commentList.adapter = adapter // Set adapter to RecyclerView

        // Observe the ratings and update UI accordingly
        viewModel.ratings.observe(viewLifecycleOwner) { ratings ->
            ratings?.let {
                adapter.submitList(it) // Update the adapter with the new ratings list

                // Calculate the average rating and update the stars UI
                val averageRating = if (it.isNotEmpty()) it.map { pair -> pair.first.rating }.average().toInt() else 0
                updateStars(averageRating)
            }
        }

        viewModel.loadRatingsByPhotoUrl(photoUrl) // Load ratings for the given photo

        // Listen for updates from the comment fragment and reload ratings
        parentFragmentManager.setFragmentResultListener("update_comments", viewLifecycleOwner) { _, _ ->
            viewModel.loadRatingsByPhotoUrl(photoUrl)
        }

        // Open the CommentFragment when the "Add Comment" button is clicked
        binding.btnAddComment.setOnClickListener {
            val modalFragment = CommentFragment()
            val bundle = Bundle().apply {
                putString("photoUrl", args.photoUrl)
            }
            modalFragment.arguments = bundle
            modalFragment.show(parentFragmentManager, "ModalFragment")
        }
    }

    // ViewHolder for displaying a rating and its associated client name
    inner class RatingViewHolder(private val binding: FragmentCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ratingWithClient: Pair<Rating, String?>) {
            val rating = ratingWithClient.first
            val clientName = ratingWithClient.second ?: "Unknown Client"

            binding.comment.text = rating.comment
            binding.clientName.text = clientName
            binding.rating.text = rating.rating.toString()
        }
    }


    /**
     * Update stars
     * Updates the UI with the correct number of filled and empty stars based on the average rating.
     *
     * @param averageRating
     */
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
                stars[i].setImageResource(android.R.drawable.btn_star_big_on) // Filled star
            } else {
                stars[i].setImageResource(android.R.drawable.btn_star_big_off) // Empty star
            }
        }
    }

    /**
     * DiffUtil implementation to optimize list updates in RecyclerView.
     */
    private val ratingDiffer = object : DiffUtil.ItemCallback<Pair<Rating, String?>>() {
        override fun areItemsTheSame(oldItem: Pair<Rating, String?>, newItem: Pair<Rating, String?>) =
            oldItem.first.ratingId == newItem.first.ratingId // Compare unique rating IDs

        override fun areContentsTheSame(oldItem: Pair<Rating, String?>, newItem: Pair<Rating, String?>) =
            oldItem == newItem // Compare full object content
    }
}


