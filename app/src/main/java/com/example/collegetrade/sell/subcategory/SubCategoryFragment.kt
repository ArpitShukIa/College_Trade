package com.example.collegetrade.sell.subcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.collegetrade.databinding.FragmentSubCategoryBinding

class SubCategoryFragment : Fragment() {

    private val args: SubCategoryFragmentArgs by navArgs()

    private lateinit var binding: FragmentSubCategoryBinding

    private var categoryIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryBinding.inflate(inflater, container, false)

        categoryIndex = args.categoryIndex

        binding.topAppBar.title = getTitle()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        initRecyclerView()

        return binding.root
    }

    private fun getTitle(): CharSequence? {
        return when(categoryIndex) {
            0 -> "ACADEMIC MATERIAL"
            1 -> "VEHICLES"
            2 -> "ELECTRONIC APPLIANCES"
            3 -> "RECREATIONAL ITEMS"
            else -> ""
        }
    }

    private fun initRecyclerView() {
        val subCategoryAdapter = SubCategoryAdapter()
        subCategoryAdapter.submitList(
            getSubCategories().toList(),
            categoryIndex
        )
        binding.subcategoryList.adapter = subCategoryAdapter
    }

    private fun getSubCategories(): Array<String> {
        return when(categoryIndex) {
            0 -> arrayOf("Books", "ED Material", "Lab Coat", "Calculator", "Other")
            1 -> arrayOf("Bicycle", "Scooty", "Bike")
            2 -> arrayOf("Table Fan", "Electric Kettle", "Cooler", "Room Heater", "Extension Board", "Other")
            3 -> arrayOf("Board Game", "Card Game", "Sports Equipment", "Musical Instrument", "Other")
            else -> arrayOf()
        }
    }

}