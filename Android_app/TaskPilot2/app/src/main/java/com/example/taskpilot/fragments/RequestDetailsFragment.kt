package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.taskpilot.R
import com.example.taskpilot.RegistrationResponse
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentRequestDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestDetailsFragment : Fragment() {

    private var _binding: FragmentRequestDetailsBinding? = null
    private val binding get() = _binding!!

    private var requestId: String? = null
    private val TAG = "RequestDetailsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data passed from the dashboard
        arguments?.let {
            requestId = it.getString("REQUEST_ID")
            binding.textViewServiceName.text = it.getString("SERVICE_NAME")
            binding.textViewStatus.text = it.getString("STATUS")
            binding.textViewCustomerName.text = "Name: ${it.getString("CUSTOMER_NAME")}"
            binding.textViewCustomerPhone.text = "Phone: ${it.getString("CUSTOMER_PHONE")}"
            binding.textViewCustomerAddress.text = "Address: ${it.getString("CUSTOMER_ADDRESS")}"
            binding.textViewPreferredDate.text = "Preferred Date: ${it.getString("PREFERRED_DATE")}"
            binding.textViewProblem.text = "Problem: ${it.getString("PROBLEM_DESC")}"

            // Hide action buttons if the job is not pending
            if (it.getString("STATUS") != "Pending") {
                binding.actionButtonsLayout.visibility = View.GONE
            }
        }

        binding.buttonAccept.setOnClickListener {
            requestId?.let { id ->
                updateStatus(id, 1, "Job Accepted!") // 1 = Accepted
            }
        }

        // --- THIS IS THE UPDATED LOGIC FOR THE REJECT BUTTON ---
        binding.buttonReject.setOnClickListener {
            requestId?.let { id ->
                // Add a confirmation dialog for a better user experience
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Rejection")
                    .setMessage("Are you sure you want to reject this job request?")
                    .setPositiveButton("Yes, Reject") { _, _ ->
                        updateStatus(id, 3, "Job Rejected.") // 3 = Rejected
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun updateStatus(id: String, status: Int, successMessage: String) {
        setLoading(true)
        val call = RetrofitClient.apiService.updateRequestStatus(id, status)

        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                    // Go back to the previous screen (the dashboard)
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "API Error: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show()
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

