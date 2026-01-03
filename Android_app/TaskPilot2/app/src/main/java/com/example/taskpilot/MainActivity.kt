package com.example.taskpilot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taskpilot.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.buttonLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val call = RetrofitClient.apiService.loginUser(email, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                setLoading(false)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status == "success") {

                        if (loginResponse.user == null || loginResponse.user.id == null) {
                            Toast.makeText(this@MainActivity, "Login failed: User data is missing from server response.", Toast.LENGTH_LONG).show()
                            return
                        }

                        Toast.makeText(this@MainActivity, "Welcome, ${loginResponse.user.name}!", Toast.LENGTH_LONG).show()

                        when (loginResponse.userType) {
                            "customer" -> {
                                val intent = Intent(this@MainActivity, CustomerDashboardActivity::class.java)
                                intent.putExtra("USER_NAME", loginResponse.user.name)
                                intent.putExtra("USER_ID", loginResponse.user.id)
                                // --- THIS IS THE FIX ---
                                // We now pass the customer's email as well
                                intent.putExtra("USER_EMAIL", loginResponse.user.email)
                                startActivity(intent)
                                finish()
                            }
                            "provider" -> {
                                val intent = Intent(this@MainActivity, ProviderDashboardActivity::class.java)
                                intent.putExtra("USER_NAME", loginResponse.user.name)
                                intent.putExtra("USER_ID", loginResponse.user.id)
                                intent.putExtra("USER_EMAIL", loginResponse.user.email)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@MainActivity, "Unknown user type.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@MainActivity, loginResponse?.message ?: "Invalid credentials.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, "Network Failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
    }
}

