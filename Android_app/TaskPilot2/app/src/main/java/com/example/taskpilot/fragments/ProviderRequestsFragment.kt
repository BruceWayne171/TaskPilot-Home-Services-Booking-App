package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
// --- THE FIX: THESE IMPORTS ARE REQUIRED FOR LISTADAPTER ---
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
// -----------------------------------------------------------
import com.example.taskpilot.*
import com.example.taskpilot.databinding.FragmentProviderRequestsBinding
import com.example.taskpilot.databinding.ItemRequestCardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderRequestsFragment : Fragment() {

    private var _binding: FragmentProviderRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestsAdapter: RequestsAdapter
    private var providerId: String? = null
    private val TAG = "ProviderRequests"

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
        _binding = FragmentProviderRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        providerId?.let { fetchProviderRequests(it) } ?: Log.e(TAG, "Provider ID is null.")
    }

    private fun setupRecyclerView() {
        requestsAdapter = RequestsAdapter { request ->
            // Navigate to details screen when a request is clicked
            val detailsFragment = RequestDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("REQUEST_ID", request.requestId)
                    putString("SERVICE_NAME", request.serviceName)
                    putString("STATUS", request.status)
                    putString("CUSTOMER_NAME", request.customerName)
                    putString("CUSTOMER_PHONE", request.customerPhone)
                    putString("CUSTOMER_ADDRESS", request.customerAddress)
                    putString("PREFERRED_DATE", request.preferredDate)
                    putString("PROBLEM_DESC", request.problemDescription)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_provider, detailsFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerViewRequests.adapter = requestsAdapter
    }

    private fun fetchProviderRequests(id: String) {
        setLoading(true)
        val call = RetrofitClient.apiService.getProviderRequests(id)
        call.enqueue(object : Callback<List<ServiceRequest>> {
            override fun onResponse(call: Call<List<ServiceRequest>>, response: Response<List<ServiceRequest>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val requests = response.body()
                    if (requests.isNullOrEmpty()) {
                        binding.textViewNoJobs.visibility = View.VISIBLE
                        binding.recyclerViewRequests.visibility = View.GONE
                    } else {
                        binding.textViewNoJobs.visibility = View.GONE
                        binding.recyclerViewRequests.visibility = View.VISIBLE
                        requestsAdapter.submitList(requests)
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ServiceRequest>>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network Failure: ${t.message}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarProvider.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


// This class definition relies on the imports at the top of the file
// THE FIX 1: The ListAdapter now uses the new, unique DiffCallback name.
class RequestsAdapter(private val onClick: (ServiceRequest) -> Unit) :
    ListAdapter<ServiceRequest, RequestsAdapter.RequestViewHolder>(ProviderRequestDiffCallback) {

    class RequestViewHolder(private val binding: ItemRequestCardBinding, val onClick: (ServiceRequest) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentRequest: ServiceRequest? = null
        init {
            itemView.setOnClickListener { currentRequest?.let { onClick(it) } }
        }
        fun bind(request: ServiceRequest) {
            currentRequest = request
            binding.textViewServiceName.text = request.serviceName
            binding.textViewStatus.text = request.status
            binding.textViewCustomerName.text = request.customerName
            binding.textViewAddress.text = request.customerAddress
            binding.textViewDate.text = request.preferredDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// THE FIX 2: The object has been renamed to avoid conflicts with other fragments.
object ProviderRequestDiffCallback : DiffUtil.ItemCallback<ServiceRequest>() {
    override fun areItemsTheSame(oldItem: ServiceRequest, newItem: ServiceRequest): Boolean = oldItem.requestId == newItem.requestId
    override fun areContentsTheSame(oldItem: ServiceRequest, newItem: ServiceRequest): Boolean = oldItem == newItem
}

