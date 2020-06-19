package com.example.collegetrade.home

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.collegetrade.R
import com.example.collegetrade.data.Ad
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<Ad>?) {
    items?.let {
        (listView.adapter as? AdsAdapter)?.submitList(items)
    }
}

@BindingAdapter("image")
fun setImage(imageView: ImageView, uri: String) {
    Glide.with(imageView.context)
        .load(uri)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?,
                isFirstResource: Boolean
            ) = false

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                dataSource: DataSource?, isFirstResource: Boolean
            ): Boolean {
                imageView.background = ColorDrawable(imageView.context.getColor(R.color.white))
                return false
            }

        })
        .into(imageView)

}

@BindingAdapter("animation")
fun setAnimation(shimmerView: ShimmerFrameLayout, enabled: Boolean) {
    if (enabled)
        shimmerView.startShimmer()
    else
        shimmerView.stopShimmer()
}