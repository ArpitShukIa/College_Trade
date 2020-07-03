package com.arpit.collegetrade.home.ad

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.arpit.collegetrade.R
import com.bumptech.glide.Glide
import com.like.LikeButton

@SuppressLint("SetTextI18n")
@BindingAdapter("datePosted")
fun setDate(view: TextView, date: String?) {
    if (date.isNullOrEmpty()) return
    val a = date.indexOf('/')
    val b = date.lastIndexOf('/')
    val d = date.substring(0, a)
    val m = date.substring(a + 1, b).toInt()
    val months =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    view.text = "Ad Posted on ${months[m]} $d"
}

@BindingAdapter("adImage")
fun setImage(view: ImageView, uri: String?) {
    Glide.with(view.context)
        .load(uri)
        .into(view)
}

@BindingAdapter("sellerImage")
fun setSellerImage(view: ImageView, uri: String?) {
    Glide.with(view.context)
        .load(uri)
        .placeholder(R.drawable.default_user_image)
        .into(view)
}

@BindingAdapter("isLiked")
fun setLiked(view: LikeButton, likeTime: Long) {
    view.isLiked = likeTime != 0L
}