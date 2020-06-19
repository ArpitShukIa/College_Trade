package com.example.collegetrade.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.collegetrade.data.Ad
import com.example.collegetrade.databinding.AdItemLayoutBinding
import com.example.collegetrade.home.AdsAdapter.ViewHolder

class AdsAdapter(private val viewModel: HomeViewModel) :
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

        fun bind(viewModel: HomeViewModel, ad: Ad) {
            binding.viewModel = viewModel
            binding.ad = ad
            binding.executePendingBindings()

            binding.adLayout.setOnClickListener {
                val directions = HomeFragmentDirections.actionHomeFragmentToAdFragment(ad)
                binding.root.findNavController().navigate(directions)
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