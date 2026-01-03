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
import com.example.taskpilot.databinding.ActivityCustomerDashboardBinding
import com.example.taskpilot.fragments.AcceptedServicesFragment
import com.example.taskpilot.fragments.BookServiceFragment
import com.example.taskpilot.fragments.ProfileFragment
import com.example.taskpilot.fragments.RequestedServicesFragment

class CustomerDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityCustomerDashboardBinding
    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null // Variable to hold the email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Get all customer info passed from MainActivity
        userName = intent.getStringExtra("USER_NAME")
        userId = intent.getStringExtra("USER_ID")
        userEmail = intent.getStringExtra("USER_EMAIL") // Get the email from the intent
        updateNavHeader()

        if (savedInstanceState == null) {
            loadFragment(BookServiceFragment())
            binding.navView.setCheckedItem(R.id.nav_book_service)
        }
    }

    /**
     * Updates the text in the navigation drawer's header.
     */
    private fun updateNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val navHeaderName: TextView = headerView.findViewById(R.id.nav_header_name)
        val navHeaderEmail: TextView = headerView.findViewById(R.id.nav_header_email)

        // Use the actual name and email, with placeholders as a fallback
        navHeaderName.text = userName ?: "Guest User"
        navHeaderEmail.text = userEmail ?: "user@email.com" // Use received email
    }

    /**
     * Handles clicks on the items in the customer's navigation sidebar.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_book_service -> {
                loadFragment(BookServiceFragment())
            }
            R.id.nav_requested -> {
                loadFragment(RequestedServicesFragment())
            }
            R.id.nav_accepted -> {
                loadFragment(AcceptedServicesFragment())
            }
            R.id.nav_profile -> {
                loadFragment(ProfileFragment())
            }
            R.id.nav_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * A helper function to load fragments and pass all necessary IDs to them.
     */
    private fun loadFragment(fragment: Fragment) {
        fragment.arguments = Bundle().apply {
            // Pass the ID using both keys to ensure all fragments work
            putString("USER_ID", userId)       // For ProfileFragment
            putString("CUSTOMER_ID", userId)   // For all other customer fragments
            putString("USER_TYPE", "customer") // For ProfileFragment
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Allows user to press back to go to previous fragment
            .commit()
    }

    /**
     * Handles the back button press.
     * If the drawer is open, it closes it.
     * If fragments are on the stack, it pops them.
     * Otherwise, it exits the app (or goes to the last activity).
     */
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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

