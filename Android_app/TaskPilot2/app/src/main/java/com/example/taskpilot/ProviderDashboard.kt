package com.example.taskpilot

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.example.taskpilot.databinding.ActivityProviderDashboardBinding
import com.example.taskpilot.fragments.ProfileFragment
import com.example.taskpilot.fragments.ProviderRequestsFragment
import com.example.taskpilot.fragments.ProviderReviewsFragment
import com.example.taskpilot.fragments.UpdatePriceFragment

class ProviderDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityProviderDashboardBinding
    private var providerId: String? = null
    private var providerName: String? = null
    private var providerEmail: String? = null // Variable to hold the email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the custom toolbar as the support action bar
        setSupportActionBar(binding.toolbarProvider)

        // Setup the navigation drawer toggle (the hamburger icon)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayoutProvider, binding.toolbarProvider,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayoutProvider.addDrawerListener(toggle)
        toggle.syncState()

        // Set the listener for menu item clicks
        binding.navViewProvider.setNavigationItemSelectedListener(this)

        // Get all provider info passed from MainActivity
        providerId = intent.getStringExtra("USER_ID")
        providerName = intent.getStringExtra("USER_NAME")
        providerEmail = intent.getStringExtra("USER_EMAIL") // Get the email
        updateNavHeader()

        // Load the reviews fragment as the default screen
        if (savedInstanceState == null) {
            loadFragment(ProviderReviewsFragment())
            binding.navViewProvider.setCheckedItem(R.id.nav_view_reviews)
        }
    }

    /**
     * Updates the text in the navigation drawer's header.
     */
    private fun updateNavHeader() {
        val headerView = binding.navViewProvider.getHeaderView(0)
        val navHeaderName: TextView = headerView.findViewById(R.id.nav_header_name)
        val navHeaderEmail: TextView = headerView.findViewById(R.id.nav_header_email)

        // Use the actual name and email, with placeholders as a fallback
        navHeaderName.text = providerName ?: "Service Provider"
        navHeaderEmail.text = providerEmail ?: "provider@email.com"
    }

    /**
     * Handles clicks on the items in the provider's navigation sidebar.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_view_reviews -> {
                loadFragment(ProviderReviewsFragment())
            }
            R.id.nav_view_requests -> {
                loadFragment(ProviderRequestsFragment())
            }
            R.id.nav_update_service -> {
                loadFragment(UpdatePriceFragment())
            }
            R.id.nav_provider_profile -> {
                loadFragment(ProfileFragment())
            }
            R.id.nav_provider_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayoutProvider.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * A helper function to load fragments and pass all necessary IDs to them.
     */
    private fun loadFragment(fragment: Fragment) {
        fragment.arguments = Bundle().apply {
            // Pass the ID using both keys to ensure all fragments work
            putString("USER_ID", providerId)     // For ProfileFragment
            putString("PROVIDER_ID", providerId)   // For all other provider fragments
            putString("USER_TYPE", "provider") // For ProfileFragment
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_provider, fragment)
            .addToBackStack(null) // Allows user to press back to go to previous fragment
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutProvider.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutProvider.closeDrawer(GravityCompat.START)
        } else {
            // Handle fragment back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}

