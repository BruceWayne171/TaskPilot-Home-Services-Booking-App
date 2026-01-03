package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpilot.R
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.Service // This is your sub-service model
import com.example.taskpilot.databinding.FragmentSubServicesBinding
import com.example.taskpilot.databinding.ItemSubServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubServicesFragment : Fragment() {

    private var _binding: FragmentSubServicesBinding? = null
    private val binding get() = _binding!!

    private lateinit var subServicesAdapter: SubServicesAdapter
    private var categoryId: String? = null
    private var customerId: String? = null // To pass along to the next step
    private val TAG = "SubServicesFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get all the data passed from BookServiceFragment
        arguments?.let {
            categoryId = it.getString("CATEGORY_ID")
            customerId = it.getString("CUSTOMER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title
        val categoryName = arguments?.getString("CATEGORY_NAME")
        binding.textViewSubServiceTitle.text = categoryName ?: "Select a Service"

        setupRecyclerView()

        // Fetch the sub-services for the selected category
        categoryId?.let {
            fetchSubServices(it)
        } ?: Toast.makeText(context, "Category ID is missing.", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        subServicesAdapter = SubServicesAdapter { subService ->
            // Click logic: navigate to ProvidersFragment
            val providersFragment = ProvidersFragment().apply {
                arguments = Bundle().apply {
                    // Pass the SUB-SERVICE ID (which is 'serviceId' in your model)
                    putString("SERVICE_ID", subService.serviceId)
                    // Pass the CUSTOMER_ID along to the next step
                    putString("CUSTOMER_ID", customerId)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, providersFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerViewSubServices.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSubServices.adapter = subServicesAdapter
    }

    private fun fetchSubServices(id: String) {
        setLoading(true)
        val call = RetrofitClient.apiService.getSubServices(id)
        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val subServices = response.body()
                    if (subServices.isNullOrEmpty()) {
                        Toast.makeText(context, "No services found for this category.", Toast.LENGTH_SHORT).show()
                    } else {
                        subServicesAdapter.submitList(subServices)
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
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

// --- Adapter for Sub-Services List ---
// This adapter binds your 'Service' (sub-service) data to the 'item_sub_service.xml' layout
class SubServicesAdapter(private val onClick: (Service) -> Unit) :
    ListAdapter<Service, SubServicesAdapter.SubServiceViewHolder>(SubServiceDiffCallback) {

    class SubServiceViewHolder(private val binding: ItemSubServiceBinding, val onClick: (Service) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentSubService: Service? = null
        init {
            itemView.setOnClickListener { currentSubService?.let { onClick(it) } }
        }
        fun bind(subService: Service) {
            currentSubService = subService
            binding.textViewSubServiceName.text = subService.serviceName
            binding.textViewSubServiceDesc.text = subService.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubServiceViewHolder {
        val binding = ItemSubServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubServiceViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: SubServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// --- DiffCallback for Sub-Services List ---
object SubServiceDiffCallback : DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean = oldItem.serviceId == newItem.serviceId
    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean = oldItem == newItem
}

