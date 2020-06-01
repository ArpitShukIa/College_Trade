package com.example.collegetrade

import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.collegetrade.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "TAG MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setAnimation()
        setupBottomNavigation()
        setupFab()
    }

    private fun setupFab() {
        binding.sellFab.setOnClickListener {
            findNavController(R.id.nav_host).navigate(R.id.sellFragment)
        }
    }

    private fun setupBottomNavigation() {
        val navController = Navigation.findNavController(this, R.id.nav_host)
        NavigationUI.setupWithNavController(bottom_navigation, navController)
    }

    private fun setAnimation() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade
    }

    override fun onBackPressed() {
        if (bottom_navigation.selectedItemId == R.id.homeFragment) {
            finishAffinity()
        } else {
            bottom_navigation.selectedItemId = R.id.homeFragment
        }
    }

}
