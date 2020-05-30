package com.example.collegetrade

import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "TAG MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_sign_out.setOnClickListener {
            AuthUI.getInstance().signOut(this)
            // Start Login Flow
        }
        setAnimation()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.chats -> {
                    true
                }
                R.id.sell -> {
                    true
                }
                R.id.favorites -> {
                    true
                }
                R.id.account -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun setAnimation() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade
    }

    override fun onBackPressed() {
        finishAffinity()
    }

}
