package com.example.barberapp

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.barberapp.Home.HomeClientFragment
import com.example.barberapp.Login.LoginFragment
import com.example.barberapp.MyAppointmentsBarber.AppointmentDetailsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNav)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility = if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment || destination.id == R.id.cameraFragment || destination.id == R.id.photoPreviewFragment) View.GONE else View.VISIBLE
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (UserSession.isLoggedInAsBarber) {
                        navController.navigate(R.id.homeBarberFragment)
                    } else {
                        navController.navigate(R.id.homeClientFragment)
                    }
                    true
                }
                R.id.appointment -> {
                    if (UserSession.isLoggedInAsBarber) {
                        navController.navigate(R.id.barberAppointmentsFragment)
                    } else {
                        navController.navigate(R.id.appointmentsFragment)
                    }
                    true
                }
                R.id.gallery -> {
                    if (UserSession.isLoggedInAsBarber) {
                        navController.navigate(R.id.galleryFragment)
                    } else {
                        navController.navigate(R.id.clientGalleryFragment)
                    }
                    true
                }
                R.id.about -> {
                    if (UserSession.isLoggedInAsBarber) {
                        navController.navigate(R.id.mapsFragment)
                    } else {
                        navController.navigate(R.id.mapsFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }

    /**
     * On destroy
     * Override of the `onDestroy` method, executed when the fragment is destroyed.
     * This method handles cleaning up the user's session and stored preferences based on
     * the "keep_logged_in" setting. It also logs relevant details for debugging purposes
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "destroying ${UserSession.loggedInBarber!!.barberId}")

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isKeepLoggedIn = sharedPreferences.getBoolean("keep_logged_in", false)

        var prefs = getSharedPreferences("user_prefs", MODE_PRIVATE).all

        if (!isKeepLoggedIn) {
            Log.d("Preferences", "Current preferences: $prefs")
            sharedPreferences.edit().clear().apply()
            UserSession.clearSession()
        }

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE).all

        Log.d("destroy", "destroyed ${UserSession.loggedInBarber?.barberId}")
        Log.d("Preferences", "Current after destroy preferences: $prefs")
    }

    /**
     * Hide keyboard when touching outside EditText
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus is EditText) {
            hideKeyboard()
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Function to hide the keyboard
     */
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


}