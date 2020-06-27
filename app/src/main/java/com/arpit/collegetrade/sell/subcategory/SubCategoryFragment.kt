package com.arpit.collegetrade.sell.subcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.arpit.collegetrade.databinding.FragmentSubCategoryBinding

class SubCategoryFragment : Fragment() {

    private val args: SubCategoryFragmentArgs by navArgs()

    private var _binding: FragmentSubCategoryBinding? = null
    private val binding get() = _binding!!

    private var categoryIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubCategoryBinding.inflate(inflater, container, false)

        categoryIndex = args.ad.category.toInt()

        binding.topAppBar.title = getTitle()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        val subCategoryAdapter = SubCategoryAdapter()
        subCategoryAdapter.submitList(
            getSubCategories().toList(),
            args.ad
        )
        binding.subcategoryList.adapter = subCategoryAdapter
    }

    private fun getTitle(): String {
        return when (categoryIndex) {
            0 -> "ACADEMIC MATERIAL"
            1 -> "VEHICLES"
            2 -> "ELECTRONIC APPLIANCES"
            3 -> "RECREATIONAL ITEMS"
            else -> ""
        }
    }

    private fun getSubCategories(): Array<String> {
        return when (categoryIndex) {
            0 -> arrayOf("Books", "ED Material", "Lab Coat", "Calculator", "Other")
            1 -> arrayOf("Bicycle", "Scooty", "Bike")
            2 -> arrayOf(
                "Table Fan",
                "Electric Kettle",
                "Cooler",
                "Room Heater",
                "Extension Board",
                "Other"
            )
            3 -> arrayOf(
                "Board Game",
                "Card Game",
                "Sports Equipment",
                "Musical Instrument",
                "Other"
            )
            else -> arrayOf()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
