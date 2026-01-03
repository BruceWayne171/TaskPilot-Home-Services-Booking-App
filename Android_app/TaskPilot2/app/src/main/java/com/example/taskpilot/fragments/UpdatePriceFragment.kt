package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskpilot.R
import com.example.taskpilot.RegistrationResponse
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.User
import com.example.taskpilot.databinding.FragmentUpdatePriceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePriceFragment : Fragment() {

    private var _binding: FragmentUpdatePriceBinding? = null
    private val binding get() = _binding!!

    private var providerId: String? = null
    private val TAG = "UpdatePriceFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            providerId = it.getString("PROVIDER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the provider's current data first
        fetchCurrentPrice()

        binding.buttonUpdatePrice.setOnClickListener {
            handlePriceUpdate()
        }
    }

    /**
     * Fetches the provider's profile to get their current price.
     */
    private fun fetchCurrentPrice() {
        if (providerId == null) {
            Toast.makeText(context, "Error: Could not identify provider.", Toast.LENGTH_LONG).show()
            return
        }
        setLoading(true)
        val call = RetrofitClient.apiService.getProfile(providerId!!, "provider")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val user = response.body()
                    // Display the current price
                    binding.textViewCurrentPrice.text = "₹${user?.pricePerHour ?: "0.00"}/hr"
                } else {
                    Toast.makeText(context, "Could not load current price.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network error fetching profile: ${t.message}")
            }
        })
    }

    /**
     * Handles submitting the new price to the server.
     */
    private fun handlePriceUpdate() {
        val newPrice = binding.editTextPrice.text.toString().trim()

        if (newPrice.isEmpty()) {
            Toast.makeText(context, "Please enter a new price.", Toast.LENGTH_SHORT).show()
            return
        }
        if (providerId == null) {
            Toast.makeText(context, "Error: Could not identify provider.", Toast.LENGTH_LONG).show()
            return
        }

        setLoading(true)
        val call = RetrofitClient.apiService.updateServicePrice(providerId!!, newPrice)
        call.enqueue(object: Callback<RegistrationResponse> {
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Price updated successfully!", Toast.LENGTH_LONG).show()
                    // Refresh the screen to show the new "Current Price"
                    fetchCurrentPrice()
                    binding.editTextPrice.text = null // Clear the input field
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to update price."
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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