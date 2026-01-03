package com.example.taskpilot
import com.google.gson.annotations.SerializedName

/**
 * Data class to model the JSON response from the register_user.php script.
 * It captures the status of the registration attempt (e.g., "success", "error")
 * and a corresponding message from the server.
 */
data class RegistrationResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String
)
