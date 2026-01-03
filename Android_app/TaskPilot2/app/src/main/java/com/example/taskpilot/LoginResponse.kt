package com.example.taskpilot

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    val user: User?, // This correctly references your User.kt data class

    @SerializedName("user_type")
    val userType: String?
)

