package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpilot.ProviderReview
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentProviderReviewsBinding
import com.example.taskpilot.databinding.ItemReviewCardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderReviewsFragment : Fragment() {

    private var _binding: FragmentProviderReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewsAdapter: ReviewsAdapter
    private var providerId: String? = null
    private val TAG = "ProviderReviewsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            providerId = it.getString("PROVIDER_ID")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        providerId?.let { fetchProviderReviews(it) }
    }

    private fun setupRecyclerView() {
        reviewsAdapter = ReviewsAdapter()
        binding.recyclerViewReviews.adapter = reviewsAdapter
    }

    private fun fetchProviderReviews(id: String) {
        setLoading(true)
        val call = RetrofitClient.apiService.getProviderReviews(id)
        call.enqueue(object : Callback<List<ProviderReview>> {
            override fun onResponse(call: Call<List<ProviderReview>>, response: Response<List<ProviderReview>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val reviews = response.body()
                    if (reviews.isNullOrEmpty()) {
                        binding.textViewNoReviews.visibility = View.VISIBLE
                        binding.cardViewStats.visibility = View.GONE
                    } else {
                        binding.textViewNoReviews.visibility = View.GONE
                        binding.cardViewStats.visibility = View.VISIBLE
                        reviewsAdapter.submitList(reviews)
                        // Calculate and display the average rating
                        val averageRating = reviews.map { it.rating }.average().toFloat()
                        binding.ratingBarAverage.rating = averageRating
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<ProviderReview>>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network Failure: ${t.message}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// --- Adapter for the Reviews List ---
class ReviewsAdapter : ListAdapter<ProviderReview, ReviewsAdapter.ReviewViewHolder>(ReviewDiffCallback) {
    class ReviewViewHolder(private val binding: ItemReviewCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ProviderReview) {
            binding.textViewCustomerName.text = review.customerName
            binding.textViewComment.text = review.comment
            binding.textViewDate.text = review.reviewDate.substringBefore(" ") // Show only date part
            binding.ratingBar.rating = review.rating
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ReviewDiffCallback : DiffUtil.ItemCallback<ProviderReview>() {
    override fun areItemsTheSame(oldItem: ProviderReview, newItem: ProviderReview): Boolean = oldItem.reviewId == newItem.reviewId
    override fun areContentsTheSame(oldItem: ProviderReview, newItem: ProviderReview): Boolean = oldItem == newItem
}
