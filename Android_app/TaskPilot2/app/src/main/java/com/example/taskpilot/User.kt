package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("price_per_hour")
    val pricePerHour: String?
)

