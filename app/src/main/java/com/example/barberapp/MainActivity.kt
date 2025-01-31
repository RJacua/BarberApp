package com.example.barberapp

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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