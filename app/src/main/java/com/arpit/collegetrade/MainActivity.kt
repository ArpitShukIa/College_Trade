package com.arpit.collegetrade

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.arpit.collegetrade.chats.buy.BuyingViewModel
import com.arpit.collegetrade.chats.sell.SellingViewModel
import com.arpit.collegetrade.databinding.ActivityMainBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.navdrawer.setUpNavigationDrawer
import com.arpit.collegetrade.util.getViewModelFactory
import com.arpit.collegetrade.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import timber.log.Timber

// TODO Don't repeat database operations on configuration changes

class MainActivity : AppCompatActivity() {

    private val TAG = "TAG MainActivity"

    val buyingViewModel: BuyingViewModel by viewModels { getViewModelFactory() }
    val sellingViewModel: SellingViewModel by viewModels { getViewModelFactory() }
    private val sharedViewModel: SharedViewModel by viewModels { getViewModelFactory() }

    private lateinit var app: Application

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        app = applicationContext as Application

        updateDatabase()
        getDeviceToken()

        app.buyUnreadCount.observe(this, {
            val count = app.sellUnreadCount.value!! + it
            setBadge(count != 0)
        })
        app.sellUnreadCount.observe(this, {
            val count = app.buyUnreadCount.value!! + it
            setBadge(count != 0)
        })

        sharedViewModel.currentTime.observe(this, { })

        sharedViewModel.apply {
            if (firstTimeRefresh) {
                refreshHome()
                refreshFav()
                refreshMyAds()
                firstTimeRefresh = false
            }
        }

        buyingViewModel.chatsList.observe(this, {})
        sellingViewModel.chatsList.observe(this, {})

        val rootDestinations = setOf(
            R.id.homeFragment, R.id.allChatsFragment, R.id.favoritesFragment, R.id.myAdsFragment
        )

        binding.apply {
            val navController = findNavController(R.id.nav_host)
            bottomNavigation.setupWithNavController(navController)
            setUpNavigationDrawer(navigationDrawer, this@MainActivity)

            sellFab.setOnClickListener {
                navController.navigate(R.id.postAdFlow)
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id in rootDestinations) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    sellFab.visibility = View.VISIBLE
                    bottomNavigation.visibility = View.VISIBLE
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    sellFab.visibility = View.GONE
                    bottomNavigation.visibility = View.GONE
                }
            }
        }
    }

    private fun setBadge(showBadge: Boolean) {
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.allChatsFragment)
        badge.backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (!showBadge)
            binding.bottomNavigation.removeBadge(R.id.allChatsFragment)
    }

    private fun updateDatabase() {
        if (app.currentUser.id.isEmpty()) {
            app.currentUser.id = Firebase.auth.currentUser?.uid!!
        } else return

        sharedViewModel.getUser()
        sharedViewModel.userRetrieved.observe(this, EventObserver { retrieved ->
            if (retrieved)
                setUpNavigationDrawer(
                    binding.navigationDrawer,
                    this
                )
            else
                uploadFailed()
        })
    }

    private fun getDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.tag(TAG).e(task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result?.token.toString()
                Timber.tag(TAG).d("getDeviceToken: token = $token")
                app.deviceToken = token
                Firebase.firestore.collection("Users").document(Firebase.auth.currentUser?.uid!!)
                    .update("deviceToken", token)
            }
    }

    private fun uploadFailed() {
        Firebase.auth.signOut()
        showToast(this, getString(R.string.sign_in_failed))
        startActivity(Intent(this, SplashScreenActivity::class.java))
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("refresh", false)
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START))
            binding.drawer.closeDrawers()
        else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        app.isActivityRunning = true
        sharedViewModel.updateLastSeen(true)
    }

    override fun onStop() {
        super.onStop()
        app.isActivityRunning = false
        sharedViewModel.updateLastSeen(false)
    }
}