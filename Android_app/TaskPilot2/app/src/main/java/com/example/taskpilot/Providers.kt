package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class Provider(
    @SerializedName("provider_id")
    val providerId: String,
    @SerializedName("provider_name")
    val providerName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("price_per_hour")
    val pricePerHour: String,
    // --- NEW FIELDS TO DISPLAY RATINGS ---
    @SerializedName("average_rating")
    val averageRating: String?, // Received as a string, can be null if no reviews

    @SerializedName("total_reviews")
    val totalReviews: Int
)
