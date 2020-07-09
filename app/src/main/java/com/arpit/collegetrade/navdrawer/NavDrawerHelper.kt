package com.arpit.collegetrade.navdrawer

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.MainActivity
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*

// TODO Design Feedback and About Fragments

fun setUpNavigationDrawer(navView: NavigationView, activity: MainActivity) {
    navView.setNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.invite -> sendInviteLink(
                activity
            )
            R.id.rate_us -> openPlayStore(
                activity
            )
            R.id.logout -> handleLogout(activity)
        }
        true
    }

    val header = navView.getHeaderView(0)
    val user = (activity.application as Application).currentUser
    header.user_name.text = user.name
    header.isClickable = false

    Glide.with(activity)
        .load(user.photo)
        .placeholder(R.drawable.default_user_image)
        .error(R.drawable.default_user_image)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?,
                target: Target<Drawable>?, isFirstResource: Boolean
            ) = false

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                dataSource: DataSource?, isFirstResource: Boolean
            ) = false.also { header.isClickable = true }
        })
        .into(header.user_image)

    header.setOnClickListener {
        header.isClickable = false
        activity.drawer.closeDrawers()

        android.os.Handler().postDelayed({
            header.isClickable = true
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build()

            activity.findNavController(R.id.nav_host)
                .navigate(R.id.profileFragment, null, navOptions)
        }, 200)
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