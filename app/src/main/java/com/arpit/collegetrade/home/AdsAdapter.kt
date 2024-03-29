package com.arpit.collegetrade.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arpit.collegetrade.R
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.databinding.AdItemLayoutBinding
import com.arpit.collegetrade.favorites.SharedViewModel
import com.arpit.collegetrade.home.AdsAdapter.ViewHolder
import com.like.LikeButton
import com.like.OnLikeListener

class AdsAdapter(private val viewModel: SharedViewModel) :
    ListAdapter<Ad, ViewHolder>(AdDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad = getItem(position)
        holder.bind(viewModel, ad)
    }

    class ViewHolder(val binding: AdItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: SharedViewModel, ad: Ad) {
            binding.ad = ad
            binding.executePendingBindings()

            binding.favoriteIcon.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    ad.likeTime = viewModel.currentTime.value ?: 0
                    ad.likesCount++
                    viewModel.updateFavList(ad, true)
                }

                override fun unLiked(likeButton: LikeButton?) {
                    viewModel.updateFavList(ad, false)
                    ad.likesCount--
                    ad.likeTime = 0
                }
            })

            binding.adLayout.setOnClickListener {
                val args = bundleOf("ad" to ad)
                binding.root.findNavController().navigate(R.id.adFragment, args)
            }
        }
    }
}

class AdDiffCallback : DiffUtil.ItemCallback<Ad>() {
    override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean {
        return oldItem == newItem
    }
}