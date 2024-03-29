package com.arpit.collegetrade.sell.subcategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arpit.collegetrade.R
import com.arpit.collegetrade.data.Ad
import kotlinx.android.synthetic.main.subcategory_list_item.view.*

class SubCategoryAdapter : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var subCategories: List<String> = ArrayList()
    private var ad = Ad()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.subcategory_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subCategories[position], ad)
    }

    override fun getItemCount() = subCategories.size

    fun submitList(list: List<String>, ad: Ad) {
        subCategories = list
        this.ad = ad
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(subcategory: String, ad: Ad) {
            itemView.apply {
                itemView.subcategory_title.text = subcategory
                setOnClickListener {
                    ad.subCategory = "$absoluteAdapterPosition"
                    val directions = SubCategoryFragmentDirections
                        .actionSubCategoryFragmentToAdDetailsFlow(ad)
                    findNavController().navigate(directions)
                }
            }
        }
    }
}