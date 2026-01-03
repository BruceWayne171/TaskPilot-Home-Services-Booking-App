package com.example.taskpilot

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpilot.databinding.ActivityRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val TAG = "RegisterActivity"

    private var categoriesList: List<ServiceCategory> = listOf()
    private var subServicesList: List<Service> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCategoriesForSpinner()

        // Listener to show/hide fields based on user type
        binding.radioGroupUserType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioButtonProvider) {
                // Show all provider fields
                binding.textInputLayoutAddress.visibility = View.GONE
                binding.textViewCategoryLabel.visibility = View.VISIBLE
                binding.spinnerCategories.visibility = View.VISIBLE
                binding.textViewSubServiceLabel.visibility = View.VISIBLE
                binding.spinnerSubServices.visibility = View.VISIBLE
                binding.textInputLayoutPrice.visibility = View.VISIBLE // Show price field
            } else {
                // Show customer fields
                binding.textInputLayoutAddress.visibility = View.VISIBLE
                binding.textViewCategoryLabel.visibility = View.GONE
                binding.spinnerCategories.visibility = View.GONE
                binding.textViewSubServiceLabel.visibility = View.GONE
                binding.spinnerSubServices.visibility = View.GONE
                binding.textInputLayoutPrice.visibility = View.GONE // Hide price field
            }
        }

        binding.buttonRegister.setOnClickListener {
            handleRegistration()
        }
    }

    /**
     * Fetches the MAIN categories (e.g., "AC Services", "Electrical") from get_categories.php
     */
    private fun fetchCategoriesForSpinner() {
        val call = RetrofitClient.apiService.getCategories()
        call.enqueue(object : Callback<List<ServiceCategory>> {
            override fun onResponse(call: Call<List<ServiceCategory>>, response: Response<List<ServiceCategory>>) {
                if (response.isSuccessful) {
                    categoriesList = response.body() ?: listOf()
                    val categoryNames = categoriesList.map { it.categoryName }

                    val adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerCategories.adapter = adapter

                    setupCategorySpinnerListener()
                } else {
                    Log.e(TAG, "Failed to fetch categories: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<ServiceCategory>>, t: Throwable) {
                Log.e(TAG, "Network error fetching categories: ${t.message}")
            }
        })
    }

    /**
     * Sets up the listener for the first spinner (Categories).
     * When a category is selected, it triggers fetching the sub-services.
     */
    private fun setupCategorySpinnerListener() {
        binding.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (categoriesList.isNotEmpty()) {
                    val selectedCategory = categoriesList[position]
                    fetchSubServicesForSpinner(selectedCategory.categoryId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.spinnerSubServices.adapter = null
            }
        }
    }

    /**
     * Fetches the SUB-SERVICES (e.g., "AC Repair") based on the selected category.
     */
    private fun fetchSubServicesForSpinner(categoryId: String) {
        val call = RetrofitClient.apiService.getSubServices(categoryId)
        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                if (response.isSuccessful) {
                    subServicesList = response.body() ?: listOf()
                    val subServiceNames = subServicesList.map { it.serviceName }

                    val adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, subServiceNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSubServices.adapter = adapter

                    binding.spinnerSubServices.visibility = View.VISIBLE
                    binding.textViewSubServiceLabel.visibility = View.VISIBLE
                } else {
                    Log.e(TAG, "Failed to fetch sub-services: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                Log.e(TAG, "Network error fetching sub-services: ${t.message}")
            }
        })
    }


    /**
     * Handles the logic for validating and submitting the registration form.
     */
    private fun handleRegistration() {
        // --- Clear errors ---
        binding.textInputLayoutName.error = null
        binding.textInputLayoutEmail.error = null
        binding.textInputLayoutPhone.error = null
        binding.textInputLayoutAddress.error = null
        binding.textInputLayoutPassword.error = null
        binding.textInputLayoutConfirmPassword.error = null
        binding.textInputLayoutPrice.error = null // Clear price error

        // --- Gather data ---
        val name = binding.editTextName.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val phone = binding.editTextPhone.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
        val price = binding.editTextPrice.text.toString().trim() // Get price
        val userType = if (binding.radioButtonProvider.isChecked) "provider" else "customer"

        // --- Validation ---
        if (name.isEmpty()) {
            binding.textInputLayoutName.error = "Name is required"; binding.textInputLayoutName.requestFocus(); return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "A valid email is required"; binding.textInputLayoutEmail.requestFocus(); return
        }
        if (phone.isEmpty() || phone.length != 10) {
            binding.textInputLayoutPhone.error = "A 10-digit phone number is required"; binding.textInputLayoutPhone.requestFocus(); return
        }
        if (password.isEmpty() || password.length < 6) {
            binding.textInputLayoutPassword.error = "Password must be at least 6 characters"; binding.textInputLayoutPassword.requestFocus(); return
        }
        if (password != confirmPassword) {
            binding.textInputLayoutConfirmPassword.error = "Passwords do not match"; binding.textInputLayoutConfirmPassword.requestFocus(); return
        }

        // --- Prepare data ---
        val registrationData = mutableMapOf(
            "name" to name, "email" to email, "phone" to phone,
            "address" to if (userType == "customer") address else "",
            "password" to password, "user_type" to userType
        )

        if (userType == "customer" && address.isEmpty()) {
            binding.textInputLayoutAddress.error = "Address is required for customers"; binding.textInputLayoutAddress.requestFocus(); return
        }

        if (userType == "provider") {
            if (subServicesList.isEmpty() || binding.spinnerSubServices.selectedItemPosition < 0) {
                Toast.makeText(this, "Please select a specific service.", Toast.LENGTH_SHORT).show(); return
            }
            if (price.isEmpty()) {
                binding.textInputLayoutPrice.error = "Price is required for providers"; binding.textInputLayoutPrice.requestFocus(); return
            }

            // --- NEW PRICE VALIDATION ---
            val priceAsNumber = price.toDoubleOrNull()
            if (priceAsNumber == null) {
                binding.textInputLayoutPrice.error = "Please enter a valid number"; binding.textInputLayoutPrice.requestFocus(); return
            }
            if (priceAsNumber > 1000) {
                binding.textInputLayoutPrice.error = "Price cannot be more than ₹1000/hr"; binding.textInputLayoutPrice.requestFocus(); return
            }

            val selectedSubService = subServicesList[binding.spinnerSubServices.selectedItemPosition]
            registrationData["service_id"] = selectedSubService.serviceId
            registrationData["price_per_hour"] = price // Add price to the data
        }

        // --- API Call ---
        setLoading(true)
        val call = RetrofitClient.apiService.registerUser(registrationData)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                val regResponse = response.body()
                if (response.isSuccessful && regResponse?.status == "success") {
                    Toast.makeText(this@RegisterActivity, "Registration successful! Please log in.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, regResponse?.message ?: "Registration failed.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(this@RegisterActivity, "Network failure. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}