package com.example.taskpilot

import com.google.gson.annotations.SerializedName

/**
 * This data class models a main category from your 'service_categories' table.
 */
data class ServiceCategory(
    @SerializedName("category_id")
    val categoryId: String,

    @SerializedName("category_name")
    val categoryName: String
)

