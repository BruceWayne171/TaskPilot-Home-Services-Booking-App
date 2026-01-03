package com.example.taskpilot.fragments

import android.app.DatePickerDialog
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
import com.example.taskpilot.databinding.FragmentBookingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private var customerId: String? = null
    private var serviceId: String? = null
    private var providerId: String? = null
    private val TAG = "BookingFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerId = it.getString("CUSTOMER_ID")
            serviceId = it.getString("SERVICE_ID")
            providerId = it.getString("PROVIDER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSubmitRequest.setOnClickListener {
            submitRequest()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.editTextDate.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        // --- THIS IS THE UPDATED LOGIC ---

        // 1. Set the minimum date to today (prevent booking in the past)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        // 2. Calculate 15 days from now in milliseconds
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.DAY_OF_YEAR, 15)
        val maxDateInMillis = maxDateCalendar.timeInMillis

        // 3. Set the maximum selectable date on the date picker
        datePickerDialog.datePicker.maxDate = maxDateInMillis
        // ---------------------------------

        datePickerDialog.show()
    }

    private fun submitRequest() {
        val date = binding.editTextDate.text.toString().trim()
        val problem = binding.editTextProblem.text.toString().trim()

        if (date.isEmpty()) {
            Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }
        if (problem.isEmpty()) {
            Toast.makeText(context, "Please describe the problem.", Toast.LENGTH_SHORT).show()
            return
        }
        if (customerId == null || serviceId == null || providerId == null) {
            Toast.makeText(context, "Error: Missing booking information.", Toast.LENGTH_LONG).show()
            return
        }

        val requestData = mapOf(
            "customer_id" to customerId!!,
            "service_id" to serviceId!!,
            "provider_id" to providerId!!,
            "preferred_date" to date,
            "problem_description" to problem
        )

        setLoading(true)
        val call = RetrofitClient.apiService.createServiceRequest(requestData)
        call.enqueue(object: Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                val serverResponse = response.body()

                if (response.isSuccessful && serverResponse?.status == "success") {
                    Toast.makeText(context, "Request submitted successfully!", Toast.LENGTH_LONG).show()
                    // Pop the back stack twice to go back to the main service list
                    parentFragmentManager.popBackStack() // Removes BookingFragment
                    parentFragmentManager.popBackStack() // Removes ProvidersFragment
                } else {
                    // Show the specific error message from the server (e.g., "Date is too far in the future")
                    Toast.makeText(context, serverResponse?.message ?: "Failed to submit request.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "API Error: ${serverResponse?.message}")
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