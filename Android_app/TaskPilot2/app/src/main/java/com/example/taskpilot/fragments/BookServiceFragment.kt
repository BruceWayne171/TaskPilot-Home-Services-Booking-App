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
import com.example.taskpilot.R
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.ServiceCategory
import com.example.taskpilot.databinding.FragmentBookServiceBinding
import com.example.taskpilot.databinding.ItemServiceCardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookServiceFragment : Fragment() {

    private var _binding: FragmentBookServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private val TAG = "BookServiceFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            // --- THIS IS THE FIX ---
            // We must also get the customer ID to pass it along the chain.
            val customerId = activity?.intent?.getStringExtra("USER_ID")

            val subServicesFragment = SubServicesFragment().apply {
                arguments = Bundle().apply {
                    putString("CATEGORY_ID", category.categoryId)
                    putString("CATEGORY_NAME", category.categoryName)
                    putString("CUSTOMER_ID", customerId) // Pass the customer ID
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, subServicesFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerViewServices.adapter = categoryAdapter
    }

    private fun fetchCategories() {
        setLoading(true)
        val call = RetrofitClient.apiService.getCategories()
        call.enqueue(object : Callback<List<ServiceCategory>> {
            override fun onResponse(call: Call<List<ServiceCategory>>, response: Response<List<ServiceCategory>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { categoryAdapter.submitList(it) }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<ServiceCategory>>, t: Throwable) {
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

// --- Adapter and DiffCallback (No changes needed) ---
class CategoryAdapter(private val onClick: (ServiceCategory) -> Unit) :
    ListAdapter<ServiceCategory, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    class CategoryViewHolder(private val binding: ItemServiceCardBinding, val onClick: (ServiceCategory) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentCategory: ServiceCategory? = null
        init {
            itemView.setOnClickListener { currentCategory?.let { onClick(it) } }
        }
        fun bind(category: ServiceCategory) {
            currentCategory = category
            binding.textViewServiceName.text = category.categoryName
            val iconResId = when {
                category.categoryName.lowercase().contains("ac") -> R.drawable.ic_ac_service
                category.categoryName.lowercase().contains("electrical") -> R.drawable.ic_electrical
                category.categoryName.lowercase().contains("home repair") -> R.drawable.ic_service_placeholder

                else -> R.drawable.ic_service_placeholder
            }
            binding.imageViewServiceIcon.setImageResource(iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemServiceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object CategoryDiffCallback : DiffUtil.ItemCallback<ServiceCategory>() {
    override fun areItemsTheSame(oldItem: ServiceCategory, newItem: ServiceCategory): Boolean = oldItem.categoryId == newItem.categoryId
    override fun areContentsTheSame(oldItem: ServiceCategory, newItem: ServiceCategory): Boolean = oldItem == newItem
}

