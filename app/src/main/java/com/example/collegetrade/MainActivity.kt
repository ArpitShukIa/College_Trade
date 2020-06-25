package com.example.collegetrade

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.collegetrade.data.User
import com.example.collegetrade.databinding.ActivityMainBinding
import com.example.collegetrade.favorites.HomeFavSharedViewModel
import com.example.collegetrade.util.getViewModelFactory
import com.example.collegetrade.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val viewModel: HomeFavSharedViewModel by viewModels { getViewModelFactory() }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        updateDatabase()

        if (savedInstanceState == null || !savedInstanceState.containsKey("refresh")) {
            viewModel.refreshHome()
            viewModel.refreshFav()
        }

        val rootDestinations = setOf(
            R.id.homeFragment,
            R.id.chatsFragment,
            R.id.favoritesFragment
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

        // Deep Link scenario
        val adId = intent.getStringExtra("adId")
        if (!adId.isNullOrEmpty()) {
            val bundle = bundleOf("adId" to adId)
            navController.navigate(R.id.adFragment, bundle)
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