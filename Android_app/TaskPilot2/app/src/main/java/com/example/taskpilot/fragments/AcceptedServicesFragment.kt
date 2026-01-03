package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.taskpilot.CustomerRequest
import com.example.taskpilot.R
import com.example.taskpilot.RegistrationResponse
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentAcceptedServicesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptedServicesFragment : Fragment() {

    private var _binding: FragmentAcceptedServicesBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookingsAdapter: AcceptedServicesAdapter
    private var customerId: String? = null
    private val TAG = "AcceptedServices"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerId = it.getString("CUSTOMER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcceptedServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        customerId?.let { fetchAcceptedServices(it) }
    }

    private fun setupRecyclerView() {
        bookingsAdapter = AcceptedServicesAdapter(
            onReviewClick = { request ->
                val reviewFragment = SubmitReviewFragment().apply {
                    arguments = Bundle().apply {
                        putString("BOOKING_ID", request.requestId)
                        putString("CUSTOMER_ID", customerId)
                        putString("PROVIDER_ID", request.providerId)
                        putString("SERVICE_NAME", request.serviceName)
                        putString("PROVIDER_NAME", request.providerName)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, reviewFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onCompleteClick = { request ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Completion")
                    .setMessage("Are you sure this service has been completed?")
                    .setPositiveButton("Yes, Complete") { _, _ ->
                        updateStatus(request.requestId, 2) // Status 2 = Completed
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerViewAccepted.adapter = bookingsAdapter
    }

    private fun updateStatus(requestId: String, status: Int) {
        setLoading(true)
        val call = RetrofitClient.apiService.updateRequestStatus(requestId, status)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Service marked as complete!", Toast.LENGTH_SHORT).show()
                    customerId?.let { fetchAcceptedServices(it) } // Refresh the list
                } else {
                    Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAcceptedServices(id: String) {
        setLoading(true)
        val call = RetrofitClient.apiService.getCustomerBookings(id)
        call.enqueue(object : Callback<List<CustomerRequest>> {
            override fun onResponse(call: Call<List<CustomerRequest>>, response: Response<List<CustomerRequest>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val bookings = response.body()
                    if (bookings.isNullOrEmpty()) {
                        binding.textViewNoBookings.visibility = View.VISIBLE
                        binding.recyclerViewAccepted.visibility = View.GONE
                    } else {
                        binding.textViewNoBookings.visibility = View.GONE
                        binding.recyclerViewAccepted.visibility = View.VISIBLE
                        bookingsAdapter.submitList(bookings)
                    }
                }
            }
            override fun onFailure(call: Call<List<CustomerRequest>>, t: Throwable) {
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

