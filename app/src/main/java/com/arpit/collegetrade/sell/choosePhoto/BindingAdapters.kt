package com.arpit.collegetrade.sell.choosePhoto

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.arpit.collegetrade.R
import com.bumptech.glide.Glide

@BindingAdapter("image")
fun setImage(imageView: ImageView, uri: Uri?) {
    Glide.with(imageView.context)
        .load(uri)
        .placeholder(R.drawable.default_placeholder)
        .into(imageView)
}

@BindingAdapter("btnAlpha")
fun setBtnAlpha(button: Button, uri: Uri?) {
    button.alpha = if (uri == "".toUri()) 1.0f else 0.8f
}

@BindingAdapter("isEnabled")
fun enableButton(button: Button, uri: Uri?) {
    if (uri == "".toUri()) {
        button.isEnabled = false
        button.alpha = 0.5f
    } else {
        button.isEnabled = true
        button.alpha = 1.0f
    }
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(imageView: ImageView, color: Int) {
    imageView.background = ColorDrawable(color)
}