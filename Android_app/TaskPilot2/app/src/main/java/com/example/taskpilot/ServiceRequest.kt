package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class ServiceRequest(
    @SerializedName("request_id")
    val requestId: String,
    @SerializedName("preferred_date")
    val preferredDate: String,
    @SerializedName("problem_description")
    val problemDescription: String,
    @SerializedName("status_text")
    val status: String,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("customer_address")
    val customerAddress: String,
    @SerializedName("customer_phone")
    val customerPhone: String,
    @SerializedName("service_name")
    val serviceName: String
)
