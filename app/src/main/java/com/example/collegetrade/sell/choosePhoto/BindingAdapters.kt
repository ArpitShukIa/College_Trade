package com.example.collegetrade.sell.choosePhoto

import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.collegetrade.R


@BindingAdapter("image")
fun setImage(imageView: ImageView, uri: Uri?) {
    Glide.with(imageView.context)
        .load(uri)
        .placeholder(R.drawable.default_placeholder)
        .into(imageView)
}

@BindingAdapter("btnAlpha")
fun setBtnAlpha(button: Button, uri: Uri?) {
    button.alpha = if (uri == null) 1.0f else 0.8f
}

@BindingAdapter("isEnabled")
fun enableButton(button: Button, uri: Uri?) {
    if (uri == null) {
        button.isEnabled = false
        button.alpha = 0.5f
    } else {
        button.isEnabled = true
        button.alpha = 1.0f
    }
}