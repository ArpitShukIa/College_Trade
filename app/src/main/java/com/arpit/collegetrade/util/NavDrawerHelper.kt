package com.arpit.collegetrade.util

import android.content.Intent
import android.net.Uri
import com.afollestad.materialdialogs.MaterialDialog
import com.arpit.collegetrade.MainActivity
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO Design Feedback and About Fragments

fun setUpNavigationDrawer(navigationView: NavigationView, activity: MainActivity) {
    navigationView.setNavigationItemSelectedListener { menuItem ->
        when(menuItem.itemId) {
            R.id.invite -> sendInviteLink(activity)
            R.id.rate_us -> openPlayStore(activity)
            R.id.logout -> handleLogout(activity)
        }
        true
    }
}

private fun sendInviteLink(activity: MainActivity) {
    // TODO Change redirect url to College Trade link
    val text = activity.getString(R.string.invite_msg)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    activity.startActivity(shareIntent)
}

private fun openPlayStore(activity: MainActivity) {
    // TODO Replace the uri with College Trade link
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://play.google.com/store/apps/details?id=com.tencent.iglite")
        setPackage("com.android.vending")
    }
    activity.startActivity(intent)
}

private fun handleLogout(activity: MainActivity) {
    MaterialDialog(activity)
        .show {
            title(R.string.are_you_sure)
            positiveButton(R.string.logout) {
                Firebase.auth.signOut()
                activity.startActivity(Intent(activity, SplashScreenActivity::class.java))
                activity.finish()
            }
            negativeButton(R.string.cancel)
        }
}