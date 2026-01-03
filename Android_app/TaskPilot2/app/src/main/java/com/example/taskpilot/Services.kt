package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("service_id")
    val serviceId: String,

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("description")
    val description: String
)
