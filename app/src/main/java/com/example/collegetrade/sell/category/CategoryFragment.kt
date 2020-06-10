package com.example.collegetrade.sell.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.collegetrade.R
import com.example.collegetrade.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private var doneNavigation = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        doneNavigation = false

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_global_homeFragment)
                }
            })

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.apply {
            academicCard.setOnClickListener(this@CategoryFragment)
            vehicleCard.setOnClickListener(this@CategoryFragment)
            electronicsCard.setOnClickListener(this@CategoryFragment)
            recreationalCard.setOnClickListener(this@CategoryFragment)
            othersCard.setOnClickListener(this@CategoryFragment)
        }

        return binding.root
    }

    override fun onClick(v: View?) {
        if (doneNavigation) return
        doneNavigation = true
        val category = when (v!!.id) {
            R.id.academic_card -> 0
            R.id.vehicle_card -> 1
            R.id.electronics_card -> 2
            R.id.recreational_card -> 3
            else -> 4
        }
        val directions =
            if (category < 4)
                CategoryFragmentDirections.actionCategoryFragmentToSubCategoryFragment(category)
            else
                CategoryFragmentDirections.actionCategoryFragmentToAdDetailsFragment(category)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
