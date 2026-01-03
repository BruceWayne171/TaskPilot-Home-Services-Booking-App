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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpilot.Provider
import com.example.taskpilot.R
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.databinding.FragmentProvidersBinding
import com.example.taskpilot.databinding.ItemProviderCardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProvidersFragment : Fragment() {

    private var _binding: FragmentProvidersBinding? = null
    private val binding get() = _binding!!
    private lateinit var providersAdapter: ProvidersAdapter

    private var serviceId: String? = null // This is the sub-service ID
    private var customerId: String? = null
    private val TAG = "ProvidersFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive the IDs from SubServicesFragment
        arguments?.let {
            serviceId = it.getString("SERVICE_ID") // This is the sub-service ID
            customerId = it.getString("CUSTOMER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProvidersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Check if the serviceId is valid before fetching providers
        serviceId?.let {
            fetchProviders(it)
        } ?: Toast.makeText(context, "Service ID is missing.", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        providersAdapter = ProvidersAdapter { provider ->
            // Click logic: Navigate to the final BookingFragment
            val bookingFragment = BookingFragment().apply {
                arguments = Bundle().apply {
                    putString("SERVICE_ID", serviceId) // Pass the sub-service ID
                    putString("CUSTOMER_ID", customerId)
                    putString("PROVIDER_ID", provider.providerId)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, bookingFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerViewProviders.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProviders.adapter = providersAdapter
    }

    private fun fetchProviders(id: String) {
        setLoading(true)
        // Call the API to get providers for the selected sub-service
        val call = RetrofitClient.apiService.getProvidersForService(id)
        call.enqueue(object : Callback<List<Provider>> {
            override fun onResponse(call: Call<List<Provider>>, response: Response<List<Provider>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val providers = response.body()
                    if (providers.isNullOrEmpty()) {
                        binding.textViewNoProviders.visibility = View.VISIBLE
                        binding.recyclerViewProviders.visibility = View.GONE
                    } else {
                        binding.textViewNoProviders.visibility = View.GONE
                        binding.recyclerViewProviders.visibility = View.VISIBLE
                        providersAdapter.submitList(providers)
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Provider>>, t: Throwable) {
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

// --- Adapter for Providers List (Includes Rating Logic) ---
class ProvidersAdapter(private val onClick: (Provider) -> Unit) :
    ListAdapter<Provider, ProvidersAdapter.ProviderViewHolder>(ProviderDiffCallback) {

    class ProviderViewHolder(private val binding: ItemProviderCardBinding, val onClick: (Provider) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentProvider: Provider? = null
        init {
            itemView.setOnClickListener { currentProvider?.let { onClick(it) } }
        }

        fun bind(provider: Provider) {
            currentProvider = provider
            binding.textViewProviderName.text = provider.providerName
            binding.textViewPhone.text = provider.phoneNumber
            binding.textViewPrice.text = "₹${provider.pricePerHour}/hr"

            // Set the star rating
            val rating = provider.averageRating?.toFloatOrNull() ?: 0f
            binding.ratingBarProvider.rating = rating

            // Set the total reviews text
            if (provider.totalReviews > 0) {
                binding.textViewTotalReviews.text = "(${provider.totalReviews} reviews)"
            } else {
                binding.textViewTotalReviews.text = "(No reviews yet)"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val binding = ItemProviderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProviderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ProviderDiffCallback : DiffUtil.ItemCallback<Provider>() {
    override fun areItemsTheSame(oldItem: Provider, newItem: Provider): Boolean = oldItem.providerId == newItem.providerId
    override fun areContentsTheSame(oldItem: Provider, newItem: Provider): Boolean = oldItem == newItem
}