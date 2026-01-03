package com.example.taskpilot.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskpilot.RegistrationResponse
import com.example.taskpilot.RetrofitClient
import com.example.taskpilot.User
import com.example.taskpilot.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var userId: String? = null
    private var userType: String? = null
    private val TAG = "ProfileFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("USER_ID")
            userType = it.getString("USER_TYPE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != null && userType != null) {
            fetchProfileData()
        } else {
            Toast.makeText(context, "Error: User data not found.", Toast.LENGTH_LONG).show()
        }

        binding.buttonSaveChanges.setOnClickListener {
            handleUpdateProfile()
        }
    }

    private fun fetchProfileData() {
        setLoading(true)
        val call = RetrofitClient.apiService.getProfile(userId!!, userType!!)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        // Pre-fill the form with existing data
                        binding.editTextName.setText(user.name)
                        binding.editTextEmail.setText(user.email)
                        binding.editTextPhone.setText(user.phoneNumber)

                        // Only show the address field if the user is a customer
                        if (userType == "customer") {
                            binding.textInputLayoutAddress.visibility = View.VISIBLE
                            binding.editTextAddress.setText(user.address)
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to load profile.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network failure: ${t.message}")
            }
        })
    }

    private fun handleUpdateProfile() {
        val name = binding.editTextName.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val phone = binding.editTextPhone.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val profileData = mutableMapOf(
            "user_id" to userId!!,
            "user_type" to userType!!,
            "name" to name,
            "email" to email,
            "phone" to phone
        )
        if (userType == "customer") {
            profileData["address"] = address
        }

        setLoading(true)
        val call = RetrofitClient.apiService.updateProfile(profileData)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                setLoading(false)
                val updateResponse = response.body()
                if (response.isSuccessful && updateResponse?.status == "success") {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack() // Go back to the dashboard
                } else {
                    Toast.makeText(context, updateResponse?.message ?: "Failed to update profile.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
