package com.example.collegetrade.sell.subcategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.collegetrade.R
import kotlinx.android.synthetic.main.subcategory_list_item.view.*

class SubCategoryAdapter : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var subcategories: List<String> = ArrayList()
    private var category_index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.subcategory_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subcategories[position], category_index)
    }

    override fun getItemCount() = subcategories.size

    fun submitList(list: List<String>, index: Int) {
        subcategories = list
        category_index = index
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(subcategory: String, category_index: Int) {

            itemView.apply {
                subcategory_title.text = subcategory
                setOnClickListener {
                    val action = SubCategoryFragmentDirections
                        .actionSubCategoryFragmentToAdDetailsFragment(
                            category_index,
                            adapterPosition
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }
}