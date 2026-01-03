package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskpilot.RegistrationResponse
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentSubmitReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubmitReviewFragment : Fragment() {

    private var _binding: FragmentSubmitReviewBinding? = null
    private val binding get() = _binding!!

    private var bookingId: String? = null
    private var customerId: String? = null
    private var providerId: String? = null
    private val TAG = "SubmitReviewFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive all the necessary IDs and names from the previous screen
        arguments?.let {
            bookingId = it.getString("BOOKING_ID")
            customerId = it.getString("CUSTOMER_ID")
            providerId = it.getString("PROVIDER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmitReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the subtitle to provide context to the user
        val serviceName = arguments?.getString("SERVICE_NAME")
        val providerName = arguments?.getString("PROVIDER_NAME")
        binding.textViewForService.text = "For your $serviceName service with '$providerName'"

        binding.buttonSubmitReview.setOnClickListener {
            submitReview()
        }
    }

    private fun submitReview() {
        val rating = binding.ratingBar.rating
        val comment = binding.editTextComment.text.toString().trim()

        // Input validation
        if (rating == 0f) {
            Toast.makeText(context, "Please select a star rating.", Toast.LENGTH_SHORT).show(); return
        }
        if (comment.isEmpty()) {
            Toast.makeText(context, "Please enter a comment.", Toast.LENGTH_SHORT).show(); return
        }
        if (bookingId == null || customerId == null || providerId == null) {
            Toast.makeText(context, "Error: Missing review information.", Toast.LENGTH_LONG).show(); return
        }

        // Prepare the data to be sent to the server in a Map
        val reviewData = mapOf(
            "booking_id" to bookingId!!,
            "customer_id" to customerId!!,
            "provider_id" to providerId!!,
            "rating" to rating.toInt().toString(),
            "comment" to comment
        )

        setLoading(true)
        val call = RetrofitClient.apiService.submitReview(reviewData)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_LONG).show()
                    // Go back to the previous screen (the accepted services list)
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to submit review."
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "API Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
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

