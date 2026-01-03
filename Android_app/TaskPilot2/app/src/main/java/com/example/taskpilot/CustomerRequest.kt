package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class CustomerRequest(
    @SerializedName("request_id")
    val requestId: String,

    @SerializedName("provider_id")
    val providerId: String?,

    @SerializedName("preferred_date")
    val preferredDate: String,

    @SerializedName("status_text")
    val status: String,

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("provider_name")
    val providerName: String?
)

