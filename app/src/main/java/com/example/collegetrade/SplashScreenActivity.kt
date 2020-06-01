package com.example.collegetrade

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.collegetrade.databinding.ActivitySplashScreenBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "TAG SplashScreen"

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        setAnimation()

        Handler().postDelayed({
            if (firebaseAuth.currentUser == null) {
                startSignInFlow()
                binding.appName.visibility = View.INVISIBLE
                binding.appTagLine.visibility = View.INVISIBLE
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }, 1000)

    }

    private fun startSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.app_icon)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun setAnimation() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.exitTransition = fade
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            when {
                resultCode == Activity.RESULT_OK -> {
                    Log.d(TAG, "onActivityResult: Successfully signed in")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                response == null -> {
                    Log.d(TAG, "onActivityResult: back button pressed")
                    Handler().postDelayed({
                        finish()
                    }, 100)
                }
                else -> {
                    Log.d(TAG, "onActivityResult: ${response.error}")
                }
            }
        }
    }
}
