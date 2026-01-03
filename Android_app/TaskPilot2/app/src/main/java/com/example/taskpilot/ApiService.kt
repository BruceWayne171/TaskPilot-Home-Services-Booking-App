package com.example.taskpilot

import retrofit2.Call
import retrofit2.http.*

/**
 * This interface defines all the API endpoints for the Task Pilot app.
 * Retrofit uses this "menu" to generate the network request code.
 */
interface ApiService {

    // --- User Authentication ---

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("registration.php")
    fun registerUser(
        @Body data: Map<String, String>
    ): Call<RegistrationResponse>


    // --- Customer Dashboard & Booking Flow ---

    /**
     * Fetches the MAIN categories from the 'service_categories' table.
     * Used in: RegisterActivity, BookServiceFragment
     */
    @GET("get_categories.php")
    fun getCategories(): Call<List<ServiceCategory>>

    /**
     * Fetches the SUB-SERVICES from the 'services' table based on a category_id.
     * Used in: RegisterActivity, SubServicesFragment
     */
    @GET("get_sub_services.php")
    fun getSubServices(@Query("category_id") categoryId: String): Call<List<Service>>

    @GET("get_services.php")
    fun getAllServices(): Call<List<Service>>

    @GET("get_requests.php")
    fun getCustomerRequests(@Query("customer_id") customerId: String): Call<List<CustomerRequest>>

    @GET("get_customer_bookings.php")
    fun getCustomerBookings(@Query("customer_id") customerId: String): Call<List<CustomerRequest>>

    @GET("get_provider_for_services.php")
    fun getProvidersForService(@Query("service_id") serviceId: String): Call<List<Provider>>

    @POST("create_request.php")
    fun createServiceRequest(@Body data: Map<String, String>): Call<RegistrationResponse>

    @POST("submit_review.php")
    fun submitReview(@Body reviewData: Map<String, String>): Call<RegistrationResponse>


    // --- Provider Dashboard & Actions ---

    @GET("get_provider_requests.php")
    fun getProviderRequests(@Query("provider_id") providerId: String): Call<List<ServiceRequest>>

    @FormUrlEncoded
    @POST("update_request.php")
    fun updateRequestStatus(
        @Field("request_id") requestId: String,
        @Field("status") status: Int
    ): Call<RegistrationResponse>

    @GET("fetch_reviews.php")
    fun getProviderReviews(@Query("provider_id") providerId: String): Call<List<ProviderReview>>

    @FormUrlEncoded
    @POST("update_service_price.php")
    fun updateServicePrice(
        @Field("provider_id") providerId: String,
        @Field("price_per_hour") newPrice: String
    ): Call<RegistrationResponse>

    // --- Shared Profile Management ---

    /**
     * Fetches the profile data for a user or provider.
     */
    @GET("get_profile.php")
    fun getProfile(
        @Query("user_id") userId: String,
        @Query("user_type") userType: String
    ): Call<User> // Re-using the User model

    /**
     * Updates the profile data for a user or provider.
     */
    @POST("update_profile.php")
    fun updateProfile(@Body profileData: Map<String, String>): Call<RegistrationResponse>
}

