package com.arpit.collegetrade

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arpit.collegetrade.databinding.ActivitySplashScreenBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "TAG SplashScreen"

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 1

    private var adId: String? = ""

    private var intentHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentHandled = true
        handleIntent(intent)
        Handler().postDelayed({
            intentHandled = false
        }, 100)
    }

    private fun handleIntent(intent: Intent?) {
        adId = intent?.data?.getQueryParameter("ad")

        if (firebaseAuth.currentUser == null)
            startSignInFlow()
        else
            launchMainActivity()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            when {
                resultCode == Activity.RESULT_OK -> {
                    Log.d(TAG, "onActivityResult: Successfully signed in")
                    launchMainActivity()
                }
                response == null -> {
                    Log.d(TAG, "onActivityResult: back button pressed or new intent found")
                    Handler().postDelayed({
                        if (!intentHandled)
                            finish()
                    }, 100)
                }
                else -> {
                    Log.d(TAG, "onActivityResult: ${response.error}")
                }
            }
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("adId", adId)
        intent.flags =
            if (adId.isNullOrEmpty())
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            else
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
