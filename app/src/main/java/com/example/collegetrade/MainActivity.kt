package com.example.collegetrade

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.collegetrade.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
}
