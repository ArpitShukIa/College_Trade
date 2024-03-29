package com.arpit.collegetrade.util

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.R
import com.arpit.collegetrade.chats.ChatsAdapter
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.data.chats.Chat
import com.arpit.collegetrade.home.AdsAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.like.LikeButton

@BindingAdapter("datePosted")
fun setDate(view: TextView, date: String?) {
    if (date.isNullOrEmpty()) return
    val a = date.indexOf('/')
    val b = date.lastIndexOf('/')
    val d = date.substring(0, a)
    val m = date.substring(a + 1, b).toInt() - 1
    val months =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    view.text = view.context.getString(R.string.ad_posted_on, months[m], d)
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

@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<Ad>?) {
    items?.let {
        if (listView.id != R.id.adsRecyclerView || items.isNotEmpty())
            (listView.adapter as? AdsAdapter)?.submitList(items)

        if (listView.tag == "scroll") {
            listView.smoothScrollToPosition(0)
            listView.tag = ""
        }

    }
}

@BindingAdapter("image")
fun setAdImage(imageView: ImageView, uri: String) {
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

@BindingAdapter("isVisible")
fun setVisibility(view: TextView, s: String?) {
    view.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("status")
fun setStatus(view: ImageView, status: Int) {
    view.setBackgroundResource(
        when (status) {
            0 -> R.drawable.ic_sending
            1 -> R.drawable.ic_sent
            2 -> R.drawable.ic_delivered
            else -> R.drawable.ic_read
        }
    )
}

@BindingAdapter("unreadCount")
fun setUnreadCount(view: TextView, chat: Chat) {
    view.visibility =
        if (chat.unreadCount == 0 || chat.isLastMsgMine)
            View.GONE else View.VISIBLE
    view.text = "${chat.unreadCount}"
}

@BindingAdapter("timestamp")
fun setTimeStamp(view: TextView, timestamp: String) {
    view.text = getTime(timestamp, 2)
}

@BindingAdapter("chats")
fun setChats(listView: RecyclerView, chats: List<Chat>?) {
    (listView.adapter as ChatsAdapter).submitList(chats)
}

@BindingAdapter("visibility")
fun setVisibility(view: ImageView, sellerId: String) {
    view.visibility =
        if ((view.context.applicationContext as Application).userId == sellerId)
            View.INVISIBLE else View.VISIBLE
}