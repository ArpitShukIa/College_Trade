package com.arpit.collegetrade.util

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun setUpNavigationDrawer(navigationView: NavigationView, fragment: Fragment) {
    navigationView.setNavigationItemSelectedListener { menuItem ->
        when(menuItem.itemId) {
            R.id.invite -> sendInviteLink(fragment)
            R.id.rate_us -> openPlayStore(fragment)
            R.id.logout -> handleLogout(fragment)
        }
        true
    }
}

private fun sendInviteLink(fragment: Fragment) {
    // TODO Change redirect url to College Trade link
    val text = fragment.getString(R.string.invite_msg)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    fragment.startActivity(shareIntent)
}

private fun openPlayStore(fragment: Fragment) {
    // TODO Replace the uri with College Trade link
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://play.google.com/store/apps/details?id=com.tencent.iglite")
        setPackage("com.android.vending")
    }
    fragment.startActivity(intent)
}

private fun handleLogout(fragment: Fragment) {
    MaterialDialog(fragment.requireContext())
        .show {
            title(R.string.are_you_sure)
            positiveButton(R.string.logout) {
                Firebase.auth.signOut()
                fragment.startActivity(Intent(fragment.activity, SplashScreenActivity::class.java))
                fragment.requireActivity().finish()
            }
            negativeButton(R.string.cancel)
        }
}