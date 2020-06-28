package com.arpit.collegetrade

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.arpit.collegetrade.data.User
import com.arpit.collegetrade.databinding.ActivityMainBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels { getViewModelFactory() }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        updateDatabase()

        viewModel.apply {
            if (firstTimeRefresh) {
                refreshHome()
                refreshFav()
                refreshMyAds()
                firstTimeRefresh = false
            }
        }


        val rootDestinations = setOf(
            R.id.homeFragment,
            R.id.chatsFragment,
            R.id.favoritesFragment,
            R.id.accountFragment
        )

        val navController = findNavController(R.id.nav_host)
        binding.bottomNavigation.setupWithNavController(navController)

        binding.sellFab.setOnClickListener {
            navController.navigate(R.id.postAdFlow)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in rootDestinations) {
                binding.sellFab.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.sellFab.visibility = View.GONE
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    private fun updateDatabase() {
        val firebaseAuth = Firebase.auth
        val currentUserId = firebaseAuth.currentUser?.uid!!

        val app = application as Application
        app.currentUserId = currentUserId
        firebaseAuth.currentUser?.displayName.also {
            if (!it.isNullOrEmpty()) app.currentUserName = it
        }

        val db = Firebase.firestore.collection("Users").document(currentUserId)

        db.get().addOnCompleteListener { task ->
            if (task.isSuccessful && !task.result!!.exists()) {
                // New User
                val user = firebaseAuth.currentUser
                var newUser = User()
                user?.apply {
                    newUser = User(uid, displayName, phoneNumber, email, photoUrl.toString())
                }

                db.set(newUser).addOnFailureListener {
                    firebaseAuth.signOut()
                    showToast(this, getString(R.string.sign_in_failed))
                    startActivity(Intent(this, SplashScreenActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("refresh", false)
    }
}