package com.example.taskpilot.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpilot.CustomerRequest
import com.example.taskpilot.databinding.ItemCustomerRequestBinding

class AcceptedServicesAdapter(
    private val onReviewClick: (CustomerRequest) -> Unit,
    private val onCompleteClick: (CustomerRequest) -> Unit
) : ListAdapter<CustomerRequest, AcceptedServicesAdapter.RequestViewHolder>(RequestDiffCallback) {

    class RequestViewHolder(
        private val binding: ItemCustomerRequestBinding,
        private val onReviewClick: (CustomerRequest) -> Unit,
        private val onCompleteClick: (CustomerRequest) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: CustomerRequest) {
            binding.textViewServiceName.text = request.serviceName
            binding.textViewStatus.text = request.status
            binding.textViewProviderName.text = request.providerName
            binding.textViewDate.text = request.preferredDate

            when (request.status) {
                "Accepted" -> {
                    binding.buttonMarkComplete.visibility = View.VISIBLE
                    binding.buttonLeaveReview.visibility = View.GONE
                    binding.buttonMarkComplete.setOnClickListener { onCompleteClick(request) }
                }
                "Completed" -> {
                    binding.buttonMarkComplete.visibility = View.GONE
                    binding.buttonLeaveReview.visibility = View.VISIBLE
                    binding.buttonLeaveReview.setOnClickListener { onReviewClick(request) }
                }
                else -> {
                    binding.buttonMarkComplete.visibility = View.GONE
                    binding.buttonLeaveReview.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemCustomerRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding, onReviewClick, onCompleteClick)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object AcceptedRequestDiffCallback : DiffUtil.ItemCallback<CustomerRequest>() {
    override fun areItemsTheSame(oldItem: CustomerRequest, newItem: CustomerRequest): Boolean = oldItem.requestId == newItem.requestId
    override fun areContentsTheSame(oldItem: CustomerRequest, newItem: CustomerRequest): Boolean = oldItem == newItem
}
