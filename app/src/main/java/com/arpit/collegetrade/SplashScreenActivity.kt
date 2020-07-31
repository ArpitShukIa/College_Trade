package com.arpit.collegetrade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arpit.collegetrade.databinding.ActivitySplashScreenBinding
import com.arpit.collegetrade.util.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "TAG SplashScreen"

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1

    private var adId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        handleIntent(intent)

        if (intent.action == Intent.ACTION_MAIN &&
            intent.categories.contains(Intent.CATEGORY_LAUNCHER)
        )
            binding.mainLayout.animate().alpha(1f).duration = 500
        else
            binding.mainLayout.alpha = 1f

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)

        binding.btnSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        adId = intent?.data?.lastPathSegment ?: ""

        Firebase.auth.currentUser?.let {
            launchMainActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val domain = account.email!!.split("@")[1]
                if (domain == "itbhu.ac.in")
                    firebaseAuthWithGoogle(account.idToken!!)
                else
                    showToast(this, "Sign in failed.\nUse your institute id instead.")
                googleSignInClient.signOut()
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.scrollView.alpha = 0.25f
        binding.progressBar.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    launchMainActivity()
                } else {
                    Log.e(TAG, "signInWithCredential: failure", task.exception)
                    showToast(this, "Authentication Failed.")
                    binding.scrollView.alpha = 1f
                    binding.progressBar.visibility = View.GONE
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