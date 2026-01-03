package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class ProviderReview(
    @SerializedName("review_id")
    val reviewId: String,

    @SerializedName("rating")
    val rating: Float,

    @SerializedName("comment")
    val comment: String,

    @SerializedName("review_date")
    val reviewDate: String,

    @SerializedName("customer_name")
    val customerName: String
)
