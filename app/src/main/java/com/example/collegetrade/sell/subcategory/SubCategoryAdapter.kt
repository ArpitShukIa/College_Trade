package com.example.collegetrade.sell.subcategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.collegetrade.R
import kotlinx.android.synthetic.main.subcategory_list_item.view.*

class SubCategoryAdapter : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var subCategories: List<String> = ArrayList()
    private var categoryIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.subcategory_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subCategories[position], categoryIndex)
    }

    override fun getItemCount() = subCategories.size

    fun submitList(list: List<String>, index: Int) {
        subCategories = list
        categoryIndex = index
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(subcategory: String, categoryIndex: Int) {

            itemView.apply {
                subcategory_title.text = subcategory
                setOnClickListener {
                    val directions = SubCategoryFragmentDirections
                        .actionSubCategoryFragmentToAdDetailsFragment(
                            categoryIndex,
                            adapterPosition
                        )
                    findNavController().navigate(directions)
                }
            }
        }
    }
}