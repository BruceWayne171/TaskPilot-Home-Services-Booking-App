package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpilot.CustomerRequest
// THE FIX: This line tells the fragment where to find RetrofitClient
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentRequestedServicesBinding
import com.example.taskpilot.databinding.ItemCustomerRequestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestedServicesFragment : Fragment() {

    private var _binding: FragmentRequestedServicesBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestsAdapter: CustomerRequestsAdapter
    private var customerId: String? = null
    private val TAG = "RequestedServices"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the customerId passed from the Activity
        arguments?.let {
            customerId = it.getString("CUSTOMER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestedServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        customerId?.let { fetchRequestedServices(it) }
    }

    private fun setupRecyclerView() {
        requestsAdapter = CustomerRequestsAdapter()
        binding.recyclerViewRequested.adapter = requestsAdapter
    }

    private fun fetchRequestedServices(id: String) {
        setLoading(true)
        // This line will now work correctly because of the import
        val call = RetrofitClient.apiService.getCustomerRequests(id)

        call.enqueue(object : Callback<List<CustomerRequest>> {
            override fun onResponse(
                call: Call<List<CustomerRequest>>,
                response: Response<List<CustomerRequest>>
            ) {
                setLoading(false)
                if (response.isSuccessful) {
                    val requests = response.body()
                    if (requests.isNullOrEmpty()) {
                        binding.textViewNoRequests.visibility = View.VISIBLE
                        binding.recyclerViewRequested.visibility = View.GONE
                    } else {
                        binding.textViewNoRequests.visibility = View.GONE
                        binding.recyclerViewRequested.visibility = View.VISIBLE
                        requestsAdapter.submitList(requests)
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                    Toast.makeText(context, "Failed to load requests.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CustomerRequest>>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network Failure: ${t.message}")
                Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show()
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


// --- Adapter for the list ---
class CustomerRequestsAdapter :
    ListAdapter<CustomerRequest, CustomerRequestsAdapter.RequestViewHolder>(RequestDiffCallback) {

    class RequestViewHolder(private val binding: ItemCustomerRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: CustomerRequest) {
            binding.textViewServiceName.text = request.serviceName
            binding.textViewDate.text = request.preferredDate
            binding.textViewProviderName.text = request.providerName ?: "Awaiting Assignment"
            // You can add more logic here to change status color, etc.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding =
            ItemCustomerRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object RequestDiffCallback : DiffUtil.ItemCallback<CustomerRequest>() {
    override fun areItemsTheSame(oldItem: CustomerRequest, newItem: CustomerRequest): Boolean {
        return oldItem.requestId == newItem.requestId
    }
    override fun areContentsTheSame(oldItem: CustomerRequest, newItem: CustomerRequest): Boolean {
        return oldItem == newItem
    }
}

